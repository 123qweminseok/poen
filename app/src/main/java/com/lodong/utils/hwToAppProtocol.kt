package com.lodong.poen.utils

import android.util.Log
import com.lodong.poen.service.BluetoothForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HwToAppProtocol(private val service: BluetoothForegroundService) {
    companion object {
        private const val TAG = "HwToAppProtocol"
        private var accumulatedBytes = 0  // 지금까지 누적된 바이트 수를 계산


        // 시작 헤더들 정의
        private val DIAGNOSIS_START_HEADER = byteArrayOf(0x02, 0x82.toByte(), 0x80.toByte(), 0x00, 0x01)
        private val SENSOR_START_HEADER = byteArrayOf(0x02, 0x82.toByte(), 0x81.toByte(), 0x00, 0x01)
        private val SENSOR_DATA_HEADER = byteArrayOf(0x02, 0x82.toByte(), 0x82.toByte(), 0x00, 0x0E)
        private val END_HEADER = byteArrayOf(0xAA.toByte(), 0xAA.toByte(), 0x03)
        // 바이트 배열을 16진수 문자열로 변환하는 유틸리티 함수
        private fun bytesToHexString(bytes: ByteArray): String {
            return bytes.joinToString(" ") { String.format("%02X", it) }
        }

    }

    var isSendingData = false


     val allCollectedData = mutableListOf<Int>()  // 전체 수집된 데이터를 저장하는 리스트

    private var isSensorDataStarted = false
    private val buffer = mutableListOf<Byte>()
    private val processedData = mutableListOf<ByteArray>()  // 전체 데이터 저장용
    private val sendData = mutableListOf<ByteArray>()       // 로그 출력용 (출력 후 초기화)
    private var logCounter = 0  // 로그 카운터 추가




    fun analyzeData(collectedData: MutableList<ByteArray>) {
        if (collectedData.isEmpty()) return

        val packet = collectedData.last()

        when {
            // 1. Diagnosis Setup Response
            packet.size >= 5 && packet.take(5).toByteArray().contentEquals(DIAGNOSIS_START_HEADER) -> {
                Log.d(TAG, "================================= Diagnosis Setup Begin ==================")

                // 전체 패킷 저장
                processedData.add(packet)

                // 로그 출력용 임시 저장
                sendData.add(packet.take(5).toByteArray())
                Log.d(TAG, "Begin Header: ${bytesToHexString(sendData.last())}")

                if (packet.size > 5) {
                    val sizeData = byteArrayOf(packet[5])
                    sendData.add(sizeData)
                    Log.d(TAG, "Size Data: ${String.format("%02X", packet[5])}")
                }

                val endHeader = packet.takeLast(3).toByteArray()
                if (endHeader.contentEquals(END_HEADER)) {
                    sendData.add(endHeader)
                    Log.d(TAG, "End Header: ${bytesToHexString(endHeader)}")
                    Log.d(TAG, "=============================== Diagnosis Setup End ===================")
                    sendData.clear()  // 로그 출력 후 초기화
                }
            }

            // 2. Sensor Setup Response
            packet.size >= 5 && packet.take(5).toByteArray().contentEquals(SENSOR_START_HEADER) -> {
                Log.d(TAG, "================================= Sensor Setup Begin ==================")

                // 전체 패킷 저장
                processedData.add(packet)

                sendData.add(packet.take(5).toByteArray())
                Log.d(TAG, "Begin Header: ${bytesToHexString(sendData.last())}")

                if (packet.size > 5) {
                    val sizeData = byteArrayOf(packet[5])
                    sendData.add(sizeData)
                    Log.d(TAG, "Size Data: ${String.format("%02X", packet[5])}")
                }

                val endHeader = packet.takeLast(3).toByteArray()
                if (endHeader.contentEquals(END_HEADER)) {
                    sendData.add(endHeader)
                    Log.d(TAG, "End Header: ${bytesToHexString(endHeader)}")
                    Log.d(TAG, "================================= Sensor Setup End ==================")
                    sendData.clear()  // 로그 출력 후 초기화
                }
            }

            // 3. Sensor Data
            packet.size >= 5 && packet.take(5).toByteArray().contentEquals(SENSOR_DATA_HEADER) -> {
                // 전체 패킷 저장
                processedData.add(packet)

                // 헤더를 아직 출력하지 않았을 때만 출력
                if (!isSensorDataStarted) {
                    Log.d(TAG, "================================= Sensor Data Begin ==================")
                    sendData.add(packet.take(5).toByteArray())
                    Log.d(TAG, "Begin Header: ${bytesToHexString(sendData.last())}")
                    isSensorDataStarted = true
                    buffer.clear()
                }

                if (packet.size > 5) {
                    val sensorData = packet.copyOfRange(5, packet.size)
                    processPacketData(sensorData)
                }
            }


        }
    }

    private fun processPacketData(data: ByteArray) {
        buffer.addAll(data.toList())

        // 17바이트씩 처리
        while (buffer.size >= 17) {
            val chunk = buffer.take(17).toByteArray()
            sendData.add(chunk)  // 로그 출력용 임시 저장
            logCounter++  // 카운터 증가
            Log.d(TAG, "데이터 확인: ${bytesToHexString(chunk)}")
//            Log.d(TAG, "Chunk count: $logCounter")  // 현재까지의 청크 수 출력

            // 데이터 수집 및 누적
            allCollectedData.addAll(chunk.map { it.toInt() }) //데이터 추가 계속
            accumulatedBytes += chunk.size // 누적 크기 증가


//            Log.d(TAG,"=======서버로 전송하는 데이터${allCollectedData}=============")
            buffer.subList(0, 17).clear()
        }

        // 데이터 전송 조건 체크 (2125바이트 이상)
        if (accumulatedBytes >= 2125 && !isSendingData) {
//            Log.d(TAG, "전송 시작: ${accumulatedBytes} bytes accumulated")
            isSendingData = true
            service.notifyDataReadyForTransfer()
            accumulatedBytes = 0  // 리셋
        }

        // END_HEADER 체크
        if (buffer.size >= 3) {
            val lastBytes = buffer.takeLast(3).toByteArray()
            if (lastBytes.contentEquals(END_HEADER)) {
                sendData.add(lastBytes)
                Log.d(TAG, "================== Sensor Data End =======================")
                Log.d(TAG, "End Header: ${bytesToHexString(lastBytes)}")
//                processFinalData()
            }
        }
    }

//    private fun processFinalData() {
//        Log.d(TAG, "=== Processing Final Data ===")
//        Log.d(TAG, "Total chunks in sendData: ${sendData.size}")
//        Log.d(TAG, "Total accumulated data: ${processedData.size}")
//
//        if (buffer.isNotEmpty()) {
//            val remainingData = buffer.toByteArray()
//            sendData.add(remainingData)
//            Log.d(TAG, "Remaining buffer: ${bytesToHexString(remainingData)}")
//            Log.d(TAG, "Remaining size: ${buffer.size}")
//        }
//
//        sendData.clear()  // 로그 출력용 데이터 초기화
//        buffer.clear()
//        isSensorDataStarted = false
//    }




}
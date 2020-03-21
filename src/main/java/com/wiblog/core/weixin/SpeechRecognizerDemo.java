//package com.wiblog.core.weixin;
//
//import com.alibaba.nls.client.AccessToken;
//import com.alibaba.nls.client.protocol.InputFormatEnum;
//import com.alibaba.nls.client.protocol.NlsClient;
//import com.alibaba.nls.client.protocol.SampleRateEnum;
//import com.alibaba.nls.client.protocol.asr.SpeechRecognizer;
//import com.alibaba.nls.client.protocol.asr.SpeechRecognizerListener;
//import com.alibaba.nls.client.protocol.asr.SpeechRecognizerResponse;
//import com.wiblog.core.entity.Voice;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 此示例演示了
// * ASR一句话识别API调用
// * 动态获取token
// * 通过本地文件模拟实时流发送
// * 识别耗时计算
// * (仅作演示，需用户根据实际情况实现)
// */
//public class SpeechRecognizerDemo {
//    private static final Logger logger = LoggerFactory.getLogger(SpeechRecognizerDemo.class);
//    private String appKey;
//    NlsClient client;
//    static Map<String, Voice> voices = new HashMap<>(16);
//
//    public SpeechRecognizerDemo(String appKey, String id, String secret) {
//        this.appKey = appKey;
//        //TODO 重要提示 创建NlsClient实例,应用全局创建一个即可,生命周期可和整个应用保持一致,默认服务地址为阿里云线上服务地址
//        //TODO 这里简单演示了获取token 的代码，该token会过期，实际使用时注意在accessToken.getExpireTime()过期前再次获取token
//        AccessToken accessToken = new AccessToken(id, secret);
//        try {
//            accessToken.apply();
//            System.out.println("get token: " + accessToken.getToken() + ", expire time: " + accessToken.getExpireTime());
//            client = new NlsClient(accessToken.getToken());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 传入自定义参数
//    private static SpeechRecognizerListener getRecognizerListener(String myOrder, String userParam) {
//        SpeechRecognizerListener listener = new SpeechRecognizerListener() {
//            //识别出中间结果.服务端识别出一个字或词时会返回此消息.仅当setEnableIntermediateResult(true)时,才会有此类消息返回
//            @Override
//            public void onRecognitionResultChanged(SpeechRecognizerResponse response) {
//                //事件名称 RecognitionResultChanged、 状态码(20000000 表示识别成功)、语音识别文本
//                System.out.println("name: " + response.getName() + ", status: " + response.getStatus() + ", result: " + response.getRecognizedText());
//            }
//
//            //识别完毕
//            @Override
//            public void onRecognitionCompleted(SpeechRecognizerResponse response) {
//                Voice voice = voices.get(myOrder);
//                voice.setMsg(response.getRecognizedText());
//                //事件名称 RecognitionCompleted, 状态码 20000000 表示识别成功, getRecognizedText是识别结果文本
//                System.out.println("name: " + response.getName() + ", status: " + response.getStatus() + ", result: " + response.getRecognizedText());
//            }
//
//            @Override
//            public void onStarted(SpeechRecognizerResponse response) {
//
//                System.out.println("myOrder: " + myOrder + "; myParam: " + userParam + "; task_id: " + response.getTaskId());
//            }
//
//            @Override
//            public void onFail(SpeechRecognizerResponse response) {
//                // TODO 重要提示： task_id很重要，是调用方和服务端通信的唯一ID标识，当遇到问题时，需要提供此task_id以便排查
//                System.out.println("task_id: " + response.getTaskId() + ", status: " + response.getStatus() + ", status_text: " + response.getStatusText());
//            }
//        };
//        return listener;
//    }
//
//    /// 根据二进制数据大小计算对应的同等语音长度
//    /// sampleRate 仅支持8000或16000
//    public static int getSleepDelta(int dataSize, int sampleRate) {
//        // 仅支持16位采样
//        int sampleBytes = 16;
//        // 仅支持单通道
//        int soundChannel = 1;
//        return (dataSize * 10 * 8000) / (160 * sampleRate);
//    }
//
//    public void process(File file, int sampleRate, String id) {
//        SpeechRecognizer recognizer = null;
//        try {
//            // 传递用户自定义参数
//            String myParam = "user-param";
//            String myOrder = id;
//            SpeechRecognizerListener listener = getRecognizerListener(myOrder, myParam);
//
//            recognizer = new SpeechRecognizer(client, listener);
//            recognizer.setAppKey(appKey);
//
//            //设置音频编码格式 TODO 如果是opus文件，请设置为 InputFormatEnum.OPUS
//            recognizer.setFormat(InputFormatEnum.PCM);
//            //设置音频采样率
//            if (sampleRate == 16000) {
//                recognizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_16K);
//            } else if (sampleRate == 8000) {
//                recognizer.setSampleRate(SampleRateEnum.SAMPLE_RATE_8K);
//            }
//            //设置是否返回中间识别结果
//            recognizer.setEnableIntermediateResult(true);
//
//            //此方法将以上参数设置序列化为json发送给服务端,并等待服务端确认
//            long now = System.currentTimeMillis();
//            recognizer.start();
//            logger.info("ASR start latency : " + (System.currentTimeMillis() - now) + " ms");
//
//            FileInputStream is = new FileInputStream(file);
//            byte[] b = new byte[3200];
//            int len;
//            while ((len = is.read(b)) > 0) {
//                logger.info("send data pack length: " + len);
//                recognizer.send(b);
//
//                // TODO  重要提示：这里是用读取本地文件的形式模拟实时获取语音流并发送的，因为read很快，所以这里需要sleep
//                // TODO  如果是真正的实时获取语音，则无需sleep, 如果是8k采样率语音，第二个参数改为8000
//                // 8000采样率情况下，3200byte字节建议 sleep 200ms，16000采样率情况下，3200byte字节建议 sleep 100ms
////                int deltaSleep = getSleepDelta(len, sampleRate);
////                Thread.sleep(deltaSleep);
//            }
//
//            //通知服务端语音数据发送完毕,等待服务端处理完成
//            now = System.currentTimeMillis();
//
//            // TODO 计算实际延迟: stop返回之后一般即是识别结果返回时间
//            logger.info("ASR wait for complete");
//            recognizer.stop();
//            logger.info("ASR stop latency : " + (System.currentTimeMillis() - now) + " ms");
//
//            is.close();
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        } finally {
//            //关闭连接
//            if (null != recognizer) {
//                recognizer.close();
//            }
//        }
//    }
//
//    public void shutdown() {
//        client.shutdown();
//    }
//}

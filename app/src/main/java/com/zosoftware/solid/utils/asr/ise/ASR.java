package com.zosoftware.solid.utils.asr.ise;

import android.content.Context;

import com.tencent.aai.AAIClient;
import com.tencent.aai.audio.data.AudioRecordDataSource;
import com.tencent.aai.auth.LocalCredentialProvider;
import com.tencent.aai.exception.ClientException;
import com.tencent.aai.exception.ServerException;
import com.tencent.aai.listener.AudioRecognizeResultListener;
import com.tencent.aai.listener.AudioRecognizeStateListener;
import com.tencent.aai.model.AudioRecognizeConfiguration;
import com.tencent.aai.model.AudioRecognizeRequest;
import com.tencent.aai.model.AudioRecognizeResult;
import com.zosoftware.solid.utils.AppConfig;
import com.zosoftware.solid.utils.Utils;

public class ASR {
    static int appid = 1254309396;
    static int projectid = 0; //濮濄倕寮弫鏉挎祼鐎规矮璐?閿?
    public interface ASRCallBack {
        void docallback(String result);
    }
    public  static ASRCallBack asrCallBack = null;
    static AAIClient aaiClient = null;

    public static void dovoicereco(Context context){
        try {
            /**閻╁瓨甯撮柎瀛樻綀**/
            // 1. 缁涙儳鎮曢柎瀛樻綀缁紮绱漵dk娑擃厾绮伴崙杞扮啊娑撯偓娑擃亝婀伴崷鎵畱闁村瓨娼堢猾浼欑礉閹劋绡冮崣顖欎簰閼奉亣顢戠€圭偟骞嘋redentialProvider閹恒儱褰涢敍灞芥躬閹劎娈戦張宥呭閸ｃ劋绗傜€圭偟骞囬柎瀛樻綀缁涙儳鎮?
            int configuredAppId = AppConfig.getInt(context, "TENCENT_ASR_APP_ID", appid);
            String secretId = AppConfig.getString(context, "TENCENT_ASR_SECRET_ID", "");
            String secretKey = AppConfig.getString(context, "TENCENT_ASR_SECRET_KEY", "");
            if (secretId.isEmpty() || secretKey.isEmpty()) {
                Utils.loginfo("Tencent ASR credentials are not configured");
                return;
            }
            aaiClient = new AAIClient(context, configuredAppId, projectid, secretId, new LocalCredentialProvider(secretKey));





            AudioRecognizeRequest.Builder builder = new AudioRecognizeRequest.Builder();
            // 2閵嗕礁鍨垫慨瀣鐠囶參鐓剁拠鍡楀焼鐠囬攱鐪伴妴?
            final AudioRecognizeRequest audioRecognizeRequest = builder
                    //鐠佸墽鐤嗛弫鐗堝祦濠ф劧绱濋弫鐗堝祦濠ф劘顩﹀Ч鍌氱杽閻滅櫃cmAudioDataSource閹恒儱褰涢敍灞惧亶閸欘垯浜掗懛顏勭箒鐎圭偟骞囧銈嗗复閸欙絾娼电€规艾鍩楅幃銊ф畱閼奉亜鐣炬稊澶嬫殶閹诡喗绨敍灞肩伐婵″倷绮犵粭顑跨瑏閺傝甯瑰ù浣疯厬閼?
                    .pcmAudioDataSource(new AudioRecordDataSource(false)) // 娴ｈ法鏁DK閸愬懐鐤嗚ぐ鏇㈢叾閸ｃ劋缍旀稉鐑樻殶閹诡喗绨?false:娑撳秳绻氱€涙﹢鐓舵０?
                    .setEngineModelType("16k_zh") // 鐠佸墽鐤嗗鏇熸惛閸欏倹鏆?"16k_zh" 闁氨鏁ゅ鏇熸惛閿涘本鏁幐浣疯厬閺傚洦娅橀柅姘崇樈+閼昏鲸鏋?
                    .setFilterDirty(0)  // 0 閿涙岸绮拋銈囧Ц閹?娑撳秷绻冨銈堝壈鐠?1閿涙俺绻冨銈堝壈鐠?
                    .setFilterModal(0) // 0 閿涙岸绮拋銈囧Ц閹?娑撳秷绻冨銈堫嚔濮樻棁鐦? 1閿涙俺绻冨銈夊劥閸掑棜顕㈠鏃囩槤 2:娑撱儲鐗告潻鍥ㄦ姢
                    .setFilterPunc(0) // 0 閿涙岸绮拋銈囧Ц閹?娑撳秷绻冨銈呭綖閺堫偆娈戦崣銉ュ娇 1閿涙碍鎶ら崣銉︽汞閻ㄥ嫬褰為崣?
                    .setConvert_num_mode(1) //1閿涙岸绮拋銈囧Ц閹?閺嶈宓侀崷鐑樻珯閺呴缚鍏樻潪顒佸床娑撴椽妯嬮幏澶夐泦閺佹澘鐡ч敍?閿涙艾鍙忛柈銊ㄦ祮娑撹桨鑵戦弬鍥ㄦ殶鐎涙ぜ鈧?
                    .setNeedvad(1) //0閿涙艾鍙ч梻?vad閿?閿涙岸绮拋銈囧Ц閹?瀵偓閸?vad閵嗗倽顕㈤棅铏闂€鑳Т鏉╁洣绔撮崚鍡涙寭闂団偓鐟曚礁绱戦崥?婵″倹鐏夌€电懓鐤勯弮鑸碘偓褑顩﹀Ч鍌濈窛妤?楠炴湹绗栭弮鍫曟？鏉堝啰鐓惃鍕翻閸?瀵ら缚顔呴崗鎶芥４
                    // .setHotWordId("")//閻戭叀鐦?id閵嗗倻鏁ゆ禍搴ょ殶閻劌顕惔鏃傛畱閻戭叀鐦濈悰顭掔礉婵″倹鐏夐崷銊ㄧ殶閻劏顕㈤棅瀹犵槕閸掝偅婀囬崝鈩冩閿涘奔绗夋潻娑滎攽閸楁洜瀚惃鍕劰鐠?id 鐠佸墽鐤嗛敍宀冨殰閸斻劎鏁撻弫鍫ョ帛鐠併倗鍎圭拠宥忕幢婵″倹鐏夋潻娑滎攽娴滃棗宕熼悪顒傛畱閻戭叀鐦?id 鐠佸墽鐤嗛敍宀勫亝娑斿牆鐨㈤悽鐔告櫏閸楁洜瀚拋鍓х枂閻ㄥ嫮鍎圭拠?id閵?
                    //.setCustomizationId("")//閼奉亜顒熸稊鐘衬侀崹?id閵嗗倸顩ч弸婊嗩啎缂冾喕绨＄拠銉ュ棘閺佸府绱濋柇锝勭疄鐏忓棛鏁撻弫鍫濐嚠鎼存梻娈戦懛顏勵劅娑旂姵膩閸?
                    .build();


            // 3閵嗕礁鍨垫慨瀣鐠囶參鐓剁拠鍡楀焼缂佹挻鐏夐惄鎴濇儔閸ｃ劊鈧?
            final AudioRecognizeResultListener audioRecognizeResultlistener = new AudioRecognizeResultListener() {


                @Override
                public void onSliceSuccess(AudioRecognizeRequest request, AudioRecognizeResult result, int seq) {
                    //鏉╂柨娲栭崚鍡欏閻ㄥ嫯鐦戦崚顐ょ波閺嬫粣绱濆銈勮礋娑擃參妫块幀浣虹波閺嬫粣绱濇导姘愁潶閹镐胶鐢绘穱顔筋劀
                }


                @Override
                public void onSegmentSuccess(AudioRecognizeRequest request, AudioRecognizeResult result, int seq) {
                    //鏉╂柨娲栫拠顓㈢叾濞翠胶娈戠拠鍡楀焼缂佹挻鐏夐敍灞绢劃娑撹櫣菙鐎规碍鈧胶绮ㄩ弸婊愮礉閸欘垰浠涙稉楦跨槕閸掝偆绮ㄩ弸婊呮暏娑撳簼绗熼崝?
                }


                @Override
                public void onSuccess(AudioRecognizeRequest request, String result) {
                    //鐠囧棗鍩嗙紒鎾存将閸ョ偠鐨熼敍宀冪箲閸ョ偞澧嶉張澶屾畱鐠囧棗鍩嗙紒鎾寸亯
                    if(asrCallBack != null){
                        asrCallBack.docallback(result);
                    }
                }


                @Override
                public void onFailure(AudioRecognizeRequest request, final ClientException clientException, final ServerException serverException, String response) {
                    // 鐠囧棗鍩嗘径杈Е
                    if(asrCallBack != null){
                        asrCallBack.docallback(response);
                    }
                }
            };


            // 4閵嗕浇鍤滅€规矮绠熺拠鍡楀焼闁板秶鐤?
            final AudioRecognizeConfiguration audioRecognizeConfiguration = new AudioRecognizeConfiguration.Builder()
                    //閸掑棛澧栨妯款吇40ms閿涘苯褰茬拋鍓х枂40-5000閿涘苯顩ч弸婊勫亶娑撳秳绨＄憴锝嗩劃閸欏倹鏆熸稉宥呯紦鐠侇喗娲块弨?
                    //.sliceTime(40)
                    // 閺勵垰鎯佹担鑳厴闂堟瑩鐓跺Λ鈧ù瀣剁礉
                    .setSilentDetectTimeOut(true)
                    // 闂堟瑩鐓跺Λ鈧ù瀣Т閺冭泛浠犲銏犵秿闂婂啿褰茬拋鍓х枂>2000ms閿涘etSilentDetectTimeOut娑撶皪rue閺堝鏅ラ敍宀冪Т鏉╁洦瀵氱€规碍妞傞梻瀛樼梾閺堝顕╃拠婵嗙殺閸忔娊妫寸拠鍡楀焼閿涙盯娓剁憰浣搞亣娴滃海鐡戞禍宸條iceTime閿涘苯鐤勯梽鍛闂傜繝璐焥liceTime閻ㄥ嫬鈧秵鏆熼敍灞筋洤閺嬫粌鐨禍宸條iceTime閿涘苯鍨幐濉籰iceTime閻ㄥ嫭妞傞梻缈犺礋閸?
                    .audioFlowSilenceTimeOut(5000)
                    // 闂婃娊鍣洪崶鐐剁殶閺冨爼妫块敍宀勬付鐟曚礁銇囨禍搴ｇ搼娴滃窏liceTime閿涘苯鐤勯梽鍛闂傜繝璐焥liceTime閻ㄥ嫬鈧秵鏆熼敍灞筋洤閺嬫粌鐨禍宸條iceTime閿涘苯鍨幐濉籰iceTime閻ㄥ嫭妞傞梻缈犺礋閸?
                    .minVolumeCallbackTime(80)
                    .build();


            // 5閵嗕礁鎯庨崝銊嚔闂婂疇鐦戦崚?
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (aaiClient!=null) {
                        aaiClient.startAudioRecognize(audioRecognizeRequest,
                                audioRecognizeResultlistener,
                                null,
                                audioRecognizeConfiguration);
                    }
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void stop(){
        Utils.loginfo("stop asr");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (aaiClient!=null){
                    //閸嬫粍顒涚拠顓㈢叾鐠囧棗鍩嗛敍宀€鐡戝鍛付缂佸牐鐦戦崚顐ょ波閺?
                    aaiClient.stopAudioRecognize();
                }
            }
        }).start();
    }
    public static void cancel(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (aaiClient!=null){
                    //閸嬫粍顒涚拠顓㈢叾鐠囧棗鍩嗛敍宀€鐡戝鍛付缂佸牐鐦戦崚顐ょ波閺?
                    aaiClient.cancelAudioRecognize();
                }
            }
        }).start();
    }
}

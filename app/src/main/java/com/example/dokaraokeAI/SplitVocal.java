package com.example.dokaraokeAI;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.example.dokaraokeAI.ui.main.MainFragment;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.stream.IntStream;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.MultimediaObject;
import it.sauronsoftware.jave.MyDefaultFFMPEGLocator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.complex.Complex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Interpreter.Options;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.TensorProcessor.Builder;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

@Metadata(
        mv = {1, 5, 1},
        k = 1,
        d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0014\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0006\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0018\n\u0002\u0010\u0013\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u001a\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J/\u0010\t\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b0\n2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000bH\u0002¢\u0006\u0002\u0010\u000fJ\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011H\u0002J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0002J\u0018\u0010\u0017\u001a\u0004\u0018\u00010\u00152\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u000eH\u0002J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J/\u0010 \u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b2\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020#H\u0002¢\u0006\u0002\u0010$JA\u0010%\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b2\u0006\u0010!\u001a\u00020\u001f2\u0018\u0010&\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000bH\u0002¢\u0006\u0002\u0010'J\u0012\u0010(\u001a\u0004\u0018\u00010\f2\u0006\u0010)\u001a\u00020\fH\u0002J4\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00190\u00192\u001e\u0010+\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000b0\u000b0\u0019H\u0002JE\u0010,\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010-0\u000b0\u000b0\u000b2\u001a\u0010.\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b0\n2\u0006\u0010\u0013\u001a\u00020\u0011H\u0002¢\u0006\u0002\u0010/JU\u00100\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000b0\u000b2\u0018\u00101\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b2\u001a\u00102\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b0\nH\u0002¢\u0006\u0002\u00103JS\u00104\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001050\u000b0\u000b0\u000b0\u000b2\u001a\u00106\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010-0\u000b0\u000b0\u000b2\u0006\u00107\u001a\u00020\u00112\u0006\u0010\u0013\u001a\u00020\u0011H\u0002¢\u0006\u0002\u00108J/\u00109\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b2\u0006\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020=H\u0002¢\u0006\u0002\u0010>J#\u0010?\u001a\u00020\u000e2\u0014\u0010@\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001050\u000b0\u000bH\u0002¢\u0006\u0002\u0010AJ'\u0010B\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000b2\u0012\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000bH\u0002¢\u0006\u0002\u0010DJ#\u0010E\u001a\u00020\u000e2\f\u0010F\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000b2\u0006\u0010G\u001a\u00020\u0011H\u0002¢\u0006\u0002\u0010HJ!\u0010I\u001a\u00020-2\u0012\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000bH\u0002¢\u0006\u0002\u0010JJ)\u0010K\u001a\u00020-2\u0012\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000b2\u0006\u0010L\u001a\u00020-H\u0002¢\u0006\u0002\u0010MJb\u0010N\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000b0\u000b0\u00192$\u0010O\u001a \u0012\u001c\u0012\u001a\u0012\u0016\u0012\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b0\u00190\u00192\u001a\u00102\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b0\nH\u0002J\u0018\u0010P\u001a\u00020\f2\u0006\u0010Q\u001a\u00020\f2\u0006\u0010R\u001a\u00020\fH\u0002J\u0010\u0010S\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\u000eH\u0002J-\u0010T\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b2\f\u0010U\u001a\b\u0012\u0004\u0012\u00020V0\u000bH\u0002¢\u0006\u0002\u0010WJ\u0016\u0010X\u001a\u00020\u001d2\u0006\u0010Y\u001a\u00020\u001f2\u0006\u0010Z\u001a\u00020[J.\u0010\\\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00190\u00192\u0018\u0010]\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u00190\u00190\u0019H\u0002JC\u0010^\u001a\u001c\u0012\u0018\u0012\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u0001050\u000b0\u000b0\u000b0\u000b2\u001a\u0010_\u001a\u0016\u0012\u0012\u0012\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b0\nH\u0002¢\u0006\u0002\u0010`J7\u0010^\u001a\b\u0012\u0004\u0012\u00020V0\u000b2\u0012\u0010C\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000b2\u0006\u0010a\u001a\u00020-2\u0006\u0010b\u001a\u00020-H\u0002¢\u0006\u0002\u0010cJE\u0010d\u001a\u00020\u000e2\u0018\u0010e\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u000b0\u000b0\u000b2\f\u0010f\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000b2\u0006\u0010L\u001a\u00020-2\u0006\u0010g\u001a\u00020-H\u0002¢\u0006\u0002\u0010hJ&\u0010i\u001a\u00020\u001d2\f\u0010j\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00192\u0006\u0010k\u001a\u00020\u00112\u0006\u0010l\u001a\u00020\u001fH\u0002J\u0006\u0010m\u001a\u00020\u001dJ/\u0010n\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b2\u0014\u0010o\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b0\u000b¢\u0006\u0002\u0010pJ\u001f\u0010n\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000b2\f\u0010o\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000b¢\u0006\u0002\u0010qJ!\u0010r\u001a\u00020\u001d2\u0012\u0010s\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\u000bH\u0002¢\u0006\u0002\u0010tR\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\b¨\u0006u"},
        d2 = {"Lcom/example/dokaraokeAI/splitVocal;", "", "()V", "jLibrosa", "Lcom/example/dokaraokeAI/JLibrosa;", "getJLibrosa", "()Lcom/example/dokaraokeAI/JLibrosa;", "setJLibrosa", "(Lcom/example/dokaraokeAI/JLibrosa;)V", "_stft", "Ljava/util/ArrayList;", "", "Lorg/apache/commons/math3/complex/Complex;", "stereoMatrix", "", "([[F)Ljava/util/ArrayList;", "computePadSize", "", "currentMatrixLen", "segmentLen", "convertBigEndianToLittleEndian", "", "value", "convertDoubleArrayToByteArray", "stereoArray", "", "deNormalizeStereoFeatureValues", "stereoFeatValues", "denoiseOutputFile", "", "outputPath", "", "executePredictionsFromTFLiteModel", "modelName", "inpByteBuffer", "Ljava/nio/ByteBuffer;", "(Ljava/lang/String;Ljava/nio/ByteBuffer;)[[[[F", "executePredictionsFromTFLiteModelAsArray", "inputInference", "(Ljava/lang/String;[[[[F)[[[[F", "expOfComplexNumber", "complexVal", "extractISTFT", "maskedResultValueList", "gen3DMatrixFrom2D", "", "mat2DValuesList", "(Ljava/util/ArrayList;I)[[[Ljava/lang/Double;", "gen3DMatrixFrom4DMatrix", "procPredMatrix", "stftValues", "([[[[FLjava/util/ArrayList;)[[[Lorg/apache/commons/math3/complex/Complex;", "gen4DMatrixFrom3D", "", "stft3DMatrixValues", "splitValue", "([[[Ljava/lang/Double;II)[[[[Ljava/lang/Double;", "gen4DMatrixFromModelOutput", "valTensorBuffer", "Lorg/tensorflow/lite/support/tensorbuffer/TensorBuffer;", "outputShape", "", "(Lorg/tensorflow/lite/support/tensorbuffer/TensorBuffer;[I)[[[[F", "genDoubleArray", "valInpArray", "([[Ljava/lang/Double;)[F", "getAngleFromComplexNumberWithTranspose", "stftComplexValues", "([[Lorg/apache/commons/math3/complex/Complex;)[[F", "getColumnFromMatrix", "DoubleMatrix", "column", "([[FI)[F", "getMean2DArray", "([[Lorg/apache/commons/math3/complex/Complex;)D", "getStdDeviation2DArray", "meanValue", "([[Lorg/apache/commons/math3/complex/Complex;D)D", "maskOutput", "matrixResultOutputList", "multiplyComplexNumbers", "complxVal1", "complxVal2", "normalizeStereoFeatureValues", "prepareInputFeatureForDenoiser", "procSTFTValues", "", "([[D)[[[[F", "processAudioSeparation", "audioFilePath", "context", "Landroid/content/Context;", "processConsolidatedInstValuesList", "consInstValuesList", "processSTFTValues", "stftValuesList", "(Ljava/util/ArrayList;)[[[[Ljava/lang/Double;", "meanSTFTVal", "stdDevSTFTVal", "([[Lorg/apache/commons/math3/complex/Complex;DD)[[D", "revertFeaturesToAudio", "consOutputFeatureValue", "angleSTFTTransposedFeatures", "stdDevVal", "([[[[F[[FDD)[F", "saveWavFromMagValues", "instrumentMagValues", "fileSuffix", "audioInputFileName", "splitVocal", "transposeMatrix", "matrix", "([[Lorg/apache/commons/math3/complex/Complex;)[[Lorg/apache/commons/math3/complex/Complex;", "([[F)[[F", "writeArrayToFile", "arr2DValue", "([[Lorg/apache/commons/math3/complex/Complex;)V", "Do_Karaoke.app"}
)
public final class SplitVocal extends AsyncTask {
    @Nullable
    private JLibrosa jLibrosa;
    private static final String MOBILE_FFMPEG_PIPE_PREFIX = "mf_pipe_";
    private static int lastCreatedPipeIndex = 0;
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private File path, targetFile;
    private static Boolean playMusic = false;

    AlertDialog dialog;
    String url;

    @Nullable
    public final JLibrosa getJLibrosa() {
        return this.jLibrosa;
    }

    public final void setJLibrosa(@Nullable JLibrosa var1) {
        this.jLibrosa = var1;
    }

    public SplitVocal(String url, final Context ctxt, AlertDialog pd) throws WavFileException, IOException, FileFormatNotSupportedException {
        this.url = url;
        this.context = ctxt;
        this.dialog = pd;
        this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/doKaraokeAI/result/");
        this.targetFile = null;
    }

    public final void processAudioSeparation(@NotNull String audioFilePath, @NotNull Context context) throws FileFormatNotSupportedException, IOException, WavFileException {
        Intrinsics.checkNotNullParameter(audioFilePath, "audioFilePath");
        Intrinsics.checkNotNullParameter(context, "context");
        Log.d("myTag000",audioFilePath);
        int defaultSampleRate = -1;
        int defaultAudioDuration = 20;
        String audioFileFullName = StringUtils.substringAfterLast(audioFilePath, "/");
        String audioFileName = StringUtils.substringBeforeLast(audioFileFullName, ".");
        this.jLibrosa = new JLibrosa();
        JLibrosa var10000 = this.jLibrosa;
        Intrinsics.checkNotNull(var10000);
        float[][] stereoFeatureValues = var10000.loadAndReadStereo(audioFilePath, defaultSampleRate, defaultAudioDuration);
        Intrinsics.checkNotNullExpressionValue(stereoFeatureValues, "stereoFeatureValues");
        float[][] stereoTransposeFeatValues = this.transposeMatrix(stereoFeatureValues);
        ArrayList stftValueList = this._stft(stereoTransposeFeatValues);
        float[][][][] stftValues = this.processSTFTValues(stftValueList);
        String[] modelNameList = new String[]{"vocals_model.tflite"};
        List predictionOutputList = (List) (new ArrayList());
        List consInstValuesStereoList = (List) (new ArrayList());
        int i = 0;
        for (int var15 = ((Object[]) stftValues).length; i < var15; ++i) {
            float[][][] valArray = stftValues[i];
            byte var18 = 1;
            float[][][][] var19 = new float[var18][][][];

            int j;
            for (j = 0; j < var18; ++j) {
                boolean var22 = false;
                short var23 = 512;
                float[][][] var24 = new float[var23][][];

                for (int var25 = 0; var25 < var23; ++var25) {
                    boolean var29 = false;
                    short var30 = 1024;
                    float[][] var31 = new float[var30][];

                    for (int var32 = 0; var32 < var30; ++var32) {
                        boolean var36 = false;
                        float[] var37 = new float[2];
                        var31[var32] = var37;
                    }

                    float[][] var38 =  var31;
                    var24[var25] = var38;
                }

                float[][][] var41 =  var24;
                var19[j] = var41;
            }

            float[][][][] inputInference = var19;
            i = 0;

            for (short var49 = 512; i < var49; ++i) {
                j = 0;

                for (short var21 = 1024; j < var21; ++j) {
                    int k = 0;

                    for (byte var53 = 2; k < var53; ++k) {
                        float[] var45 = inputInference[0][i][j];
                        float var10002 = valArray[i][j][k];
                        Intrinsics.checkNotNull(valArray[i][j][k]);
                        var45[k] = var10002;
                    }
                }
            }

            List predictionStemOutputList = (List) (new ArrayList());
            int m = 0;

            for (j = modelNameList.length; m < j; ++m) {
                float[][][][] matrixResultOutput = this.executePredictionsFromTFLiteModelAsArray(modelNameList[m], inputInference);
                predictionStemOutputList.add(matrixResultOutput);
            }

            consInstValuesStereoList.add(predictionStemOutputList);
        }

        List maskedResult = this.maskOutput(consInstValuesStereoList, stftValueList);
        List insValuesStereoList = this.extractISTFT(maskedResult);
        int p = 0;

        for (int var46 = insValuesStereoList.size(); p < var46; ++p) {
            List var10001 = (List) insValuesStereoList.get(p);
            Intrinsics.checkNotNullExpressionValue(audioFileName, "audioFileName");

            this.saveWavFromMagValues(var10001, p, audioFileName, audioFilePath);
        }

    }

    private final List processConsolidatedInstValuesList(List consInstValuesList) {
        int segmentSize = consInstValuesList.size();
        int instrumentSize = ((List) consInstValuesList.get(0)).size();
        int channelSize = ((List) ((List) consInstValuesList.get(0)).get(0)).size();
        int magSize = ((Double[]) ((List) ((List) consInstValuesList.get(0)).get(0)).get(0)).length;
        List procConsInstValuesList = (List) (new ArrayList());
        int j = 0;

        for (int var8 = instrumentSize; j < var8; ++j) {
            List channelValuesList = (List) (new ArrayList());
            int k = 0;

            for (int var11 = channelSize; k < var11; ++k) {
                ArrayList procDoubleArrayList = new ArrayList();
                int l = 0;

                for (int var14 = magSize; l < var14; ++l) {
                    int i = 0;

                    for (int var16 = segmentSize; i < var16; ++i) {
                        procDoubleArrayList.add(((Double[]) ((List) ((List) consInstValuesList.get(i)).get(j)).get(k))[l]);
                    }
                }

                channelValuesList.add(CollectionsKt.toDoubleArray((Collection) procDoubleArrayList));
            }

            procConsInstValuesList.add(channelValuesList);
        }

        return procConsInstValuesList;
    }

    private final void saveWavFromMagValues(List instrumentMagValues, int fileSuffix, String audioInputFileName, String audioInputFilePath) throws IOException {
        byte[] byteArray = this.convertDoubleArrayToByteArray(instrumentMagValues);
       // File var10000 = Environment.getExternalStorageDirectory();

     //   Intrinsics.checkNotNullExpressionValue(var10000, "Environment.getExternalStorageDirectory()");
        if(!path.exists())
            path.mkdir();
        audioInputFileName = audioInputFileName.replace(" ","");
        String audioFileName = audioInputFileName + "_karaoke";
        String outputPath_1 = path.getAbsolutePath()  + "/" + audioFileName + ".wav";
        String sampleOutputFile = path.getAbsolutePath() + "/bytes_j.txt";

      // Intrinsics.checkNotNullExpressionValue(var18, "Config.registerNewFFmpegPipe(MyApp.getContext())");
        String pipe1 = registerNewFFmpegPipe(context);
        try {
            File file = new File(outputPath_1);
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if(!file.exists())
                file.createNewFile();

            OutputStream os = new FileOutputStream(file);
            os.write(byteArray);

            String var17 = "Successfully byte inserted";
            boolean var13 = false;
            System.out.println(var17);
            os.close();
        } catch (Exception var14) {
            String var11 = "Exception: " + var14;
            boolean var12 = false;
            System.out.println(var11);
        }


        File audioInputFile = new File(audioInputFilePath);
        if(!audioInputFile.getParentFile().exists())
            audioInputFile.getParentFile().mkdirs();
        if(!audioInputFile.exists())
            audioInputFile.createNewFile();

        File audioOutputFile = new File(outputPath_1);
        if(!audioOutputFile.getParentFile().exists())
            audioOutputFile.getParentFile().mkdirs();
        if(!audioOutputFile.exists())
            audioOutputFile.createNewFile();

        String command_ = "-i " + audioInputFile + " -c:a pcm_s16le -af \"bandreject=f=900:width_type=h:w=800, bandreject=f=900:width_type=h:w=1000\" " + audioOutputFile + " -y";

        String command = "-i " + audioInputFile + " -af pan=\"stereo|c0=c0|c1=-1*c1\" -ac 2 " + audioOutputFile + " -y";
        //String command = "ffmpeg -i " + audioInputFile + " -f wav -ac 1 " + audioOutputFile;
        String ffmpegCommand = "-f f32le -ac 2 -ar 44100 -i " + pipe1 + " -b:a 128k -ar 44100 -strict -2 " + outputPath_1 + " -y";
        Runtime.getRuntime().exec(new String[]{"sh", "-c", "cat " + sampleOutputFile + " > " + pipe1});
        FFmpeg.execute(command);
        targetFile = audioOutputFile;

    }


    public static String registerNewFFmpegPipe(final Context context) {

        // PIPES ARE CREATED UNDER THE CACHE DIRECTORY
        final File cacheDir = context.getCacheDir();

        final String newFFmpegPipePath = cacheDir + File.separator + MOBILE_FFMPEG_PIPE_PREFIX + (++lastCreatedPipeIndex);

        // FIRST CLOSE OLD PIPES WITH THE SAME NAME
        closeFFmpegPipe(newFFmpegPipePath);

        int rc = registerNewNativeFFmpegPipe(newFFmpegPipePath);
        if (rc == 0) {
            return newFFmpegPipePath;
        } else {
            Log.e("error", String.format("Failed to register new FFmpeg pipe %s. Operation failed with rc=%d.", newFFmpegPipePath, rc));
            return null;
        }
    }


    private native static int registerNewNativeFFmpegPipe(final String ffmpegPipePath);


    public static void closeFFmpegPipe(final String ffmpegPipePath) {
        File file = new File(ffmpegPipePath);
        if (file.exists()) {
            file.delete();
        }
    }



   /* private final void denoiseOutputFile(String outputPath) throws FileFormatNotSupportedException, IOException, WavFileException {
        int defaultSampleRate = -1;
        int defaultAudioDuration = -1;
        JLibrosa jLibrosa = new JLibrosa();
        float[] stereoFeatureValues = jLibrosa.loadAndRead(outputPath, defaultSampleRate, defaultAudioDuration);
        Intrinsics.checkNotNullExpressionValue(stereoFeatureValues, "stereoFeatureValues");
        float[] normalizedStereoFeatureValues = this.normalizeStereoFeatureValues(stereoFeatureValues);
        int sampleRate = '걄';
        Complex[][] stftComplexValues = jLibrosa.generateSTFTFeaturesWithPadOption(normalizedStereoFeatureValues, sampleRate, 40, 256, 128, 64, true);
        Intrinsics.checkNotNullExpressionValue(stftComplexValues, "stftComplexValues");
        double meanSTFTComplexVal = this.getMean2DArray(stftComplexValues);
        double stdDevSTFTComplexVal = this.getStdDeviation2DArray(stftComplexValues, meanSTFTComplexVal);
        Double[][] phaseAngleSTFTTransposedFeatures = this.getAngleFromComplexNumberWithTranspose(stftComplexValues);
        float[][] processedSTFTComplexValues = this.processSTFTValues(stftComplexValues, meanSTFTComplexVal, stdDevSTFTComplexVal);
        double[][][][] processedInputFeatures = this.prepareInputFeatureForDenoiser(processedSTFTComplexValues);
        String denoiserModelName = "denoiser.tflite";
        int i = ((Object[]) processedInputFeatures).length;
        double[][][][] var19 = new double[i][][][];

        boolean var22;
        int var23;
        double[][][] var44;
        for (int var20 = 0; var20 < i; ++var20) {
            var22 = false;
            var23 = ((Object[]) processedInputFeatures[0]).length;
            double[][][] var24 = new double[var23][][];

            for (int var25 = 0; var25 < var23; ++var25) {
                boolean var29 = false;
                byte var30 = 1;
                double[][] var31 = new double[var30][];

                for (int var32 = 0; var32 < var30; ++var32) {
                    boolean var36 = false;
                    double[] var37 = new double[1];
                    var31[var32] = var37;
                }

                double[][] var38 = var31;
                var24[var25] = var38;
            }

            var44 = var24;
            var19[var20] = var44;
        }

        float[][][][] consOutputFromModel = (float[][][][])(Object) var19;
        i = 0;

        byte var21;
        for (int var46 = ((Object[]) processedInputFeatures).length; i < var46; ++i) {
            var21 = 1;
            double[][][][] var50 = new double[var21][][][];

            for (var23 = 0; var23 < var21; ++var23) {
                boolean var51 = false;
                int var26 = ((Object[]) processedInputFeatures[0]).length;
                double[][][] var27 = new double[var26][][];

                for (int var28 = 0; var28 < var26; ++var28) {
                    boolean var52 = false;
                    int var33 = ((Object[]) processedInputFeatures[0][0]).length;
                    double[][] var34 = new double[var33][];

                    for (int var35 = 0; var35 < var33; ++var35) {
                        boolean var39 = false;
                        double[] var40 = new double[processedInputFeatures[0][0][0].length];
                        var34[var35] = var40;
                    }

                    double[][] var41 = var34;
                    var27[var28] = var41;
                }

                var44 =  var27;
                var50[var23] = var44;
            }

            double[][][][] subsetProcInpFeatures =  var50;
            subsetProcInpFeatures[0] = processedInputFeatures[i];
            float[][][][] predResult = this.executePredictionsFromTFLiteModelAsArray(denoiserModelName, subsetProcInpFeatures);
            consOutputFromModel[i] = predResult[0];
        }

        float[] magValues = this.revertFeaturesToAudio(consOutputFromModel, phaseAngleSTFTTransposedFeatures, meanSTFTComplexVal, stdDevSTFTComplexVal);
        this.deNormalizeStereoFeatureValues(magValues);
        List mutableMagList = (List) (new ArrayList());
        mutableMagList.add(magValues);
        this.saveWavFromMagValues(mutableMagList, 0, "denoised_output");
        var21 = 1;
        var22 = false;
        System.out.print(var21);
    }
*/
    private final Double[][] getAngleFromComplexNumberWithTranspose(Complex[][] stftComplexValues) {
        int i = ((Object[]) stftComplexValues).length;
        Double[][] var4 = new Double[i][];

        int j;
        Double[] var11;
        for (j = 0; j < i; ++j) {
            boolean var7 = false;
            var11 = new Double[stftComplexValues[0].length];
            var4[j] = var11;
        }

        Double[][] angleSTFTComplexValues = (Double[][]) var4;
        i = 0;


        for (i = ((Object[]) stftComplexValues).length; i < i; ++i) {
            j = 0;

            for (j = stftComplexValues[0].length; j < j; ++j) {
                angleSTFTComplexValues[i][j] = (Double) Math.atan2(stftComplexValues[i][j].getImaginary(), stftComplexValues[i][j].getReal());
            }
        }

        i = stftComplexValues[0].length;
        Double[][] var14 = new Double[i][];

        for (j = 0; j < i; ++j) {
            boolean var8 = false;
            var11 = new Double[((Object[]) stftComplexValues).length];
            var14[j] = var11;
        }

        Double[][] transposedAngleSTFTComplexValues = (Double[][]) var14;
        i = 0;

        for (j = ((Object[]) angleSTFTComplexValues).length; i < j; ++i) {
            j = 0;

            for (int var15 = angleSTFTComplexValues[0].length; j < var15; ++j) {
                transposedAngleSTFTComplexValues[j][i] = angleSTFTComplexValues[i][j];
            }
        }

        return transposedAngleSTFTComplexValues;
    }

    private final float[] revertFeaturesToAudio(float[][][][] consOutputFeatureValue, Double[][] angleSTFTTransposedFeatures, double meanValue, double stdDevVal) {
        int i = ((Object[]) consOutputFeatureValue[0]).length;
        Complex[][] var9 = new Complex[i][];

        int j;
        int var13;
        int var15;
        for (j = 0; j < i; ++j) {
            boolean var12 = false;
            var13 = ((Object[]) consOutputFeatureValue).length;
            Complex[] var14 = new Complex[var13];

            for (var15 = 0; var15 < var13; ++var15) {
                boolean var19 = false;
                Complex var20 = new Complex(0.0D, 0.0D);
                var14[var15] = var20;
            }

            var9[j] = var14;
        }

        Complex[][] squeezedFeatureValue = (Complex[][]) var9;
        i = 0;

        for (int var25 = ((Object[]) consOutputFeatureValue).length; i < var25; ++i) {
            j = 0;

            for (int var11 = ((Object[]) consOutputFeatureValue[0]).length; j < var11; ++j) {
                int k = 0;

                for (var13 = ((Object[]) consOutputFeatureValue[0][0]).length; k < var13; ++k) {
                    int l = 0;

                    for (var15 = consOutputFeatureValue[0][0][0].length; l < var15; ++l) {
                        Complex phaseComplexConst = new Complex(0.0D, 1.0D);
                        Complex featPhaseProdValue = phaseComplexConst.multiply((double) angleSTFTTransposedFeatures[i][j]);
                        Intrinsics.checkNotNullExpressionValue(featPhaseProdValue, "featPhaseProdValue");
                        Complex expPhase = this.expOfComplexNumber(featPhaseProdValue);
                        double featValue = (double) consOutputFeatureValue[i][j][k][l] * stdDevVal + meanValue;
                        if (expPhase != null) {
                            Complex[] var10000 = squeezedFeatureValue[j];
                            Complex var10002 = expPhase.multiply(featValue);
                            Intrinsics.checkNotNullExpressionValue(var10002, "expPhase.multiply(featValue)");
                            var10000[i] = var10002;
                        }
                    }
                }
            }
        }

        JLibrosa var28 = this.jLibrosa;
        Intrinsics.checkNotNull(var28);
        float[] var29 = var28.generateInvSTFTFeaturesWithPadOption(squeezedFeatureValue, 44100, 40, 256, 128, 64, -1, true);
        Intrinsics.checkNotNullExpressionValue(var29, "jLibrosa!!.generateInvST…0,256, 128, 64, -1, true)");
        float[] magValues = var29;
        return magValues;
    }

    private final Complex expOfComplexNumber(Complex complexVal) {
        double xVal = Math.exp(complexVal.getReal());
        double yVal_1 = Math.cos(complexVal.getImaginary());
        double yVal_2 = Math.sin(complexVal.getImaginary());
        complexVal = new Complex(yVal_1, yVal_2);
        Complex resultVal = complexVal.multiply(xVal);
        return resultVal;
    }

    private final double[][][][] prepareInputFeatureForDenoiser(float[][] procSTFTValues) {
        int numFeatures = 129;
        int numSegments = 8;
        int i = ((Object[]) procSTFTValues).length;
        double[][] var6 = new double[i][];

        int j;
        for (j = 0; j < i; ++j) {
            boolean var9 = false;
            double[] var29 = new double[procSTFTValues[0].length + numSegments - 1];
            var6[j] = var29;
        }

        double[][] noisySTFT = (double[][]) var6;
        i = 0;


        for (i = ((Object[]) noisySTFT).length; i < i; ++i) {
            j = 0;

            for (j = noisySTFT[0].length; j < j; ++j) {
                int jInd = j;
                if (j >= numSegments - 1) {
                    jInd = j - (numSegments - 1);
                }

                noisySTFT[i][j] = procSTFTValues[i][jInd];
            }
        }

        i = noisySTFT[1].length - numSegments + 1;
        double[][][][] var32 = new double[i][][][];

        short var11;
        for (j = 0; j < i; ++j) {
            boolean var10 = false;
            var11 = (short) numFeatures;
            double[][][] var12 = new double[numFeatures][][];

            for (int var13 = 0; var13 < var11; ++var13) {
                boolean var17 = false;
                byte var18 = (byte) numSegments;
                double[][] var19 = new double[numSegments][];

                for (int var20 = 0; var20 < var18; ++var20) {
                    boolean var24 = false;
                    double[] var25 = new double[1];
                    var19[var20] = var25;
                }

                double[][] var26 =  var19;
                var12[var13] = var26;
            }

            double[][][] var37 = var12;
            var32[j] = var37;
        }

        double[][][][] nInpFeatureSTFT =  var32;
        i = 0;

        for (j = noisySTFT[1].length - numSegments + 1; i < j; ++i) {
            j = 0;

            for (byte var34 = (byte) numSegments; j < var34; ++j) {
                int k = 0;

                for (var11 = (short) numFeatures; k < var11; ++k) {
                    int jNew = i + j;
                    nInpFeatureSTFT[i][k][j][0] = (Double) noisySTFT[k][jNew];
                }
            }
        }

        return nInpFeatureSTFT;
    }

    private final float[][] processSTFTValues(Complex[][] stftComplexValues, double meanSTFTVal, double stdDevSTFTVal) {
        int xVal = ((Object[]) stftComplexValues).length;
        int yVal = stftComplexValues[0].length;
        float[][] var9 = new float[xVal][];

        int var10;
        for (var10 = 0; var10 < xVal; ++var10) {
            boolean var12 = false;
            float[] var17 = new float[yVal];
            var9[var10] = var17;
        }

        float[][] procSTFTArrayValue = var9;
        int i = 0;

        for (var10 = xVal; i < var10; ++i) {
            int j = 0;

            for (int var18 = yVal; j < var18; ++j) {
                double absValue = ((Complex) ((Object[]) ((Object[]) stftComplexValues)[i])[j]).abs();
                procSTFTArrayValue[i][j] = (float) ((absValue - meanSTFTVal) / stdDevSTFTVal);
            }
        }

        return procSTFTArrayValue;
    }

    private final double getMean2DArray(Complex[][] stftComplexValues) {
        int counter = 0;
        double sum = 0.0D;
        int i = 0;

        for (int var6 = ((Object[]) stftComplexValues).length; i < var6; ++i) {
            int j = 0;

            for (int var8 = ((Object[]) ((Object[]) stftComplexValues)[i]).length; j < var8; ++j) {
                double absValue = ((Complex) ((Object[]) ((Object[]) stftComplexValues)[i])[j]).abs();
                sum += absValue;
                ++counter;
            }
        }

        return sum / (double) counter;
    }

    private final double getStdDeviation2DArray(Complex[][] stftComplexValues, double meanValue) {
        double sum = 0.0D;
        int xVal = ((Object[]) stftComplexValues).length;
        int yVal = stftComplexValues[0].length;
        double[][] var9 = new double[xVal][];

        int var10;
        for (var10 = 0; var10 < xVal; ++var10) {
            boolean var12 = false;
            double[] var17 = new double[yVal];
            var9[var10] = var17;
        }

        double[][] inter2DArrayValue = (double[][]) var9;
        int i = 0;

        int j;
        int var20;
        for (var10 = xVal; i < var10; ++i) {
            j = 0;

            for (var20 = yVal; j < var20; ++j) {
                double absValue = ((Complex) ((Object[]) ((Object[]) stftComplexValues)[i])[j]).abs();
                inter2DArrayValue[i][j] = absValue - meanValue;
                inter2DArrayValue[i][j] *= inter2DArrayValue[i][j];
            }
        }

        i = 0;

        for (var10 = xVal; i < var10; ++i) {
            j = 0;

            for (var20 = yVal; j < var20; ++j) {
                sum += ((double[]) ((Object[]) inter2DArrayValue)[i])[j];
            }
        }

        double varianceVal = sum / (double) (xVal * yVal);
        double stdDevVal = Math.sqrt(varianceVal);
        return stdDevVal;
    }

    private final Double[] deNormalizeStereoFeatureValues(float[] stereoFeatValues) {
        int xVal = stereoFeatValues.length;
        double normalizeIndex = 0.3333333D;
        Double[] normalizedStereoFeatValues = new Double[xVal];
        int i = 0;

        for (int var7 = xVal; i < var7; ++i) {
            normalizedStereoFeatValues[i] = (Double) ((double) stereoFeatValues[i] / normalizeIndex);
        }

        return normalizedStereoFeatValues;
    }

    private final float[] normalizeStereoFeatureValues(float[] stereoFeatValues) {
        int xVal = stereoFeatValues.length;
        double normalizeIndex = 0.3333333D;
        float[] normalizedStereoFeatValues = new float[xVal];
        int i = 0;

        for (int var7 = xVal; i < var7; ++i) {
            normalizedStereoFeatValues[i] = (float) ((double) stereoFeatValues[i] * normalizeIndex);
        }

        return normalizedStereoFeatValues;
    }

    private final byte[] convertDoubleArrayToByteArray(List stereoArray) throws IOException {
        Log.d("myError1", String.valueOf(stereoArray.get(0)));
        Log.d("myError2", String.valueOf(stereoArray));
        Log.d("myError3", String.valueOf(stereoArray.size()));
        float[] array = (float[]) stereoArray.get(0);
     //   Arrays.fill(array, arr);
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bas);
        for (float f : array)
            ds.writeFloat(f);
        byte[] consByteArray = bas.toByteArray();
  //      int n_channels = 2;

       /* byte[] consByteArray = new byte[4 * array.length * n_channels];
        int i = 0;

        for (int var6 = array.length; i < var6; ++i) {
            int j = 0;

            for (int var8 = ((Collection) stereoArray).size(); j < var8; ++j) {
                byte[] byteArray = ByteBuffer.allocate(4).putDouble(((double[]) stereoArray.get(j))[i]).array();
                Intrinsics.checkNotNullExpressionValue(byteArray, "byteArray");
                byte[] leByteArray = this.convertBigEndianToLittleEndian(byteArray);
                int k = 0;
                Intrinsics.checkNotNull(leByteArray);

                for (int var12 = leByteArray.length; k < var12; ++k) {
                    consByteArray[i * 8 + j * 4 + k] = leByteArray[k];
                }
            }
        }
*/
        return consByteArray;
    }

    private final byte[] convertBigEndianToLittleEndian(byte[] value) {
        int length = value.length;
        byte[] res = new byte[length];
        int i = 0;

        for (int var5 = length; i < var5; ++i) {
            res[length - i - 1] = value[i];
        }

        return res;
    }

    private final void writeArrayToFile(Complex[][] arr2DValue) throws IOException {
        StringBuilder builder = new StringBuilder();
        int i = 0;

        for (int var4 = ((Object[]) arr2DValue).length; i < var4; ++i) {
            int j = 0;

            for (int var6 = arr2DValue[0].length; j < var6; ++j) {
                builder.append(((Complex) ((Object[]) ((Object[]) arr2DValue)[i])[j]).toString() + "");
                if (j < arr2DValue[0].length - 1) {
                    builder.append(",");
                }
            }

            builder.append("\n");
        }

        File var10000 = Environment.getExternalStorageDirectory();
        Intrinsics.checkNotNullExpressionValue(var10000, "Environment.getExternalStorageDirectory()");
        File externalStorage_1 = var10000;
        String sampleOutputFile = externalStorage_1.getAbsolutePath() + "/images/twodarray.txt";
        BufferedWriter writer = new BufferedWriter((Writer) (new FileWriter(sampleOutputFile)));
        writer.write(builder.toString());
        writer.close();
    }

    private final List extractISTFT(List maskedResultValueList) {
        List insValuesStereoArrayList = (List) (new ArrayList());
        int i = 0;

        for (int var4 = maskedResultValueList.size(); i < var4; ++i) {
            Complex[][][] maskedResultValue = (Complex[][][]) maskedResultValueList.get(i);
            List magValuesDoubleArrayList = (List) (new ArrayList());
            int p = 0;

            for (int var8 = maskedResultValue[0][0].length; p < var8; ++p) {
                int q = ((Object[]) maskedResultValue[0]).length;
                Complex[][] var11 = new Complex[q][];

                int r;
                for (r = 0; r < q; ++r) {
                    boolean var14 = false;
                    int var15 = ((Object[]) maskedResultValue).length;
                    Complex[] var16 = new Complex[var15];

                    for (int var17 = 0; var17 < var15; ++var17) {
                        boolean var21 = false;
                        Complex var22 = new Complex(0.0D, 0.0D);
                        var16[var17] = var22;
                    }

                    var11[r] = var16;
                }

                Complex[][] maskedResultValInstrument = (Complex[][]) var11;
                q = 0;

                for (int var27 = ((Object[]) maskedResultValue[0]).length; q < var27; ++q) {
                    r = 0;

                    for (int var13 = ((Object[]) maskedResultValue).length; r < var13; ++r) {
                        maskedResultValInstrument[q][r] = maskedResultValue[r][q][p];
                    }
                }

                JLibrosa var10000 = this.jLibrosa;
                Intrinsics.checkNotNull(var10000);
                JLibrosa var10002 = this.jLibrosa;
                Intrinsics.checkNotNull(var10002);
                float[] var28 = var10000.generateInvSTFTFeatures(maskedResultValInstrument, var10002.getSampleRate(), 40, 4096, 128, 1024);
                Intrinsics.checkNotNullExpressionValue(var28, "jLibrosa!!.generateInvST…Rate, 40,4096, 128, 1024)");
                float[] magValues = var28;
                magValuesDoubleArrayList.add(magValues);
            }

            insValuesStereoArrayList.add(magValuesDoubleArrayList);
        }

        return insValuesStereoArrayList;
    }

    private final float[][][][] executePredictionsFromTFLiteModelAsArray(String modelName, float[][][][] inputInference) throws IOException {
        Interpreter tflite = null;
        MappedByteBuffer var10000 = FileUtil.loadMappedFile(context, modelName);
        Intrinsics.checkNotNullExpressionValue(var10000, "FileUtil.loadMappedFile(….getContext(), modelName)");
        MappedByteBuffer tfliteModel = var10000;
        Options tfliteOptions = new Options();
        tfliteOptions.setNumThreads(2);
        tflite = new Interpreter(tfliteModel, tfliteOptions);
        int imageTensorIndex = 0;
        Intrinsics.checkNotNullExpressionValue(tflite.getInputTensor(imageTensorIndex).dataType(), "tflite.getInputTensor(imageTensorIndex).dataType()");
        Intrinsics.checkNotNullExpressionValue(tflite.getInputTensor(imageTensorIndex).shape(), "tflite.getInputTensor(imageTensorIndex).shape()");
        int probabilityTensorIndex = 0;
        int[] outputDataShape = tflite.getOutputTensor(probabilityTensorIndex).shape();
        Intrinsics.checkNotNullExpressionValue(tflite.getOutputTensor(probabilityTensorIndex).dataType(), "tflite.getOutputTensor(p…tyTensorIndex).dataType()");
        int var13 = outputDataShape[0];
        float[][][][] var14 = new float[var13][][][];

        for (int var15 = 0; var15 < var13; ++var15) {
            boolean var17 = false;
            int var18 = outputDataShape[1];
            float[][][] var19 = new float[var18][][];

            for (int var20 = 0; var20 < var18; ++var20) {
                boolean var24 = false;
                int var25 = outputDataShape[2];
                float[][] var26 = new float[var25][];

                for (int var27 = 0; var27 < var25; ++var27) {
                    boolean var31 = false;
                    float[] var32 = new float[outputDataShape[3]];
                    var26[var27] = var32;
                }

                float[][] var33 = var26;
                var19[var20] = var33;
            }

            float[][][] var36 =  var19;
            var14[var15] = var36;
        }

        float[][][][] outputInference = var14;
        tflite.run(inputInference, outputInference);
        return outputInference;
    }

    private final Double[][][][] executePredictionsFromTFLiteModel(String modelName, ByteBuffer inpByteBuffer) throws IOException {
        Interpreter tflite = null;
        MappedByteBuffer var10000 = FileUtil.loadMappedFile(context, modelName);
        Intrinsics.checkNotNullExpressionValue(var10000, "FileUtil.loadMappedFile(….getContext(), modelName)");
        MappedByteBuffer tfliteModel = var10000;
        Options tfliteOptions = new Options();
        tfliteOptions.setNumThreads(2);
        tflite = new Interpreter((ByteBuffer) tfliteModel, tfliteOptions);
        int imageTensorIndex = 0;
        Intrinsics.checkNotNullExpressionValue(tflite.getInputTensor(imageTensorIndex).dataType(), "tflite.getInputTensor(imageTensorIndex).dataType()");
        Intrinsics.checkNotNullExpressionValue(tflite.getInputTensor(imageTensorIndex).shape(), "tflite.getInputTensor(imageTensorIndex).shape()");
        int probabilityTensorIndex = 0;
        int[] probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape();
        DataType var16 = tflite.getOutputTensor(probabilityTensorIndex).dataType();
        Intrinsics.checkNotNullExpressionValue(var16, "tflite.getOutputTensor(p…tyTensorIndex).dataType()");
        DataType probabilityDataType = var16;
        TensorBuffer var17 = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        Intrinsics.checkNotNullExpressionValue(var17, "TensorBuffer.createFixed…ape, probabilityDataType)");
        TensorBuffer outputTensorBuffer = var17;
        tflite.run(inpByteBuffer, outputTensorBuffer.getBuffer());
        TensorProcessor var18 = (new Builder()).build();
        Intrinsics.checkNotNullExpressionValue(var18, "TensorProcessor.Builder()\n                .build()");
        TensorProcessor outputProcessor = var18;
        Object var19 = outputProcessor.process(outputTensorBuffer);
        Intrinsics.checkNotNullExpressionValue(var19, "outputProcessor.process(outputTensorBuffer)");
        TensorBuffer modelOutputResult = (TensorBuffer) var19;
        Intrinsics.checkNotNullExpressionValue(probabilityShape, "probabilityShape");
        Double[][][][] matrixResultOutput = this.gen4DMatrixFromModelOutput(modelOutputResult, probabilityShape);
        return matrixResultOutput;
    }

    private final List maskOutput(List matrixResultOutputList, ArrayList stftValues) {
        int separationComponent = 1;
        Double eps = Double.valueOf(1.0E-10F);
        int lenStems = 1;
        int pInd = matrixResultOutputList.size();
        int qInd = ((Object[]) ((float[][][][]) ((List) matrixResultOutputList.get(0)).get(0))[0]).length;
        int rInd = ((Object[]) ((float[][][][]) ((List) matrixResultOutputList.get(0)).get(0))[0][0]).length;
        int sInd = ((float[][][][]) ((List) matrixResultOutputList.get(0)).get(0))[0][0][0].length;
        int instrumentOutputSize = ((List) matrixResultOutputList.get(0)).size();
        List processedPredOutputList = (List) (new ArrayList());
        int x = 0;

        for (int var13 = instrumentOutputSize; x < var13; ++x) {
            int rInd_extn = 2049;
            Double[][][][] var16 = new Double[pInd][][][];

            int var17;
            int k;
            int l;
            for (var17 = 0; var17 < pInd; ++var17) {
                boolean var19 = false;
                k = qInd;
                Double[][][] var21 = new Double[qInd][][];

                for (l = 0; l < k; ++l) {
                    boolean var26 = false;
                    short var27 = (short) rInd_extn;
                    Double[][] var28 = new Double[rInd_extn][];

                    for (int var29 = 0; var29 < var27; ++var29) {
                        boolean var33 = false;
                        Double[] var34 = new Double[sInd];
                        var28[var29] = var34;
                    }

                    Double[][] var35 = (Double[][]) var28;
                    var21[l] = var35;
                }

                Double[][][] var38 = (Double[][][]) var21;
                var16[var17] = var38;
            }

            Double[][][][] procPredMatrix = (Double[][][][]) var16;
            int i = 0;

            for (var17 = pInd; i < var17; ++i) {
                int j = 0;

                for (int var41 = qInd; j < var41; ++j) {
                    k = 0;

                    for (int var42 = rInd; k < var42; ++k) {
                        l = 0;

                        for (int var23 = sInd; l < var23; ++l) {
                            float value = ((float[][][][]) ((List) matrixResultOutputList.get(i)).get(x))[0][j][k][l];
                            boolean var43 = false;
                            Double procValue = (float) Math.pow((float) value, (float) separationComponent) + eps / lenStems;
                            procPredMatrix[i][j][k][l] = procValue;
                        }
                    }
                }
            }

            Complex[][][] procPred3DMatrix = this.gen3DMatrixFrom4DMatrix(procPredMatrix, stftValues);
            processedPredOutputList.add(procPred3DMatrix);
        }

        return processedPredOutputList;
    }

    private final Complex[][][] gen3DMatrixFrom4DMatrix(Double[][][][] procPredMatrix, ArrayList stftValues) {
        int pInd = ((Object[]) procPredMatrix).length;
        int qInd = ((Object[]) procPredMatrix[0]).length;
        int rInd = ((Object[]) procPredMatrix[0][0]).length;
        int sInd = procPredMatrix[0][0][0].length;
        int stftVal1DSize = ((Object[]) stftValues.get(0)).length;
        Complex[][][] var9 = new Complex[stftVal1DSize][][];

        int p;
        int var13;
        int var15;
        for (p = 0; p < stftVal1DSize; ++p) {
            boolean var12 = false;
            var13 = ((Object[]) procPredMatrix[0][0]).length;
            Complex[][] var14 = new Complex[var13][];

            for (var15 = 0; var15 < var13; ++var15) {
                boolean var19 = false;
                int var20 = procPredMatrix[0][0][0].length;
                Complex[] var21 = new Complex[var20];

                for (int var22 = 0; var22 < var20; ++var22) {
                    boolean var26 = false;
                    Complex var27 = new Complex(0.0D, 0.0D);
                    var21[var22] = var27;
                }

                var14[var15] = var21;
            }

            Complex[][] var31 = (Complex[][]) var14;
            var9[p] = var31;
        }

        Complex[][][] procPred3DMatrix = (Complex[][][]) var9;
        boolean breakFlag = false;
        p = 0;

        for (int var11 = pInd; p < var11; ++p) {
            int q = 0;

            for (var13 = qInd; q < var13; ++q) {
                int r = 0;

                for (var15 = rInd; r < var15; ++r) {
                    int s = 0;

                    for (int var17 = sInd; s < var17; ++s) {
                        int pqInd = p * q + q;
                        if (pqInd >= stftVal1DSize) {
                            breakFlag = true;
                            break;
                        }

                        double dblVal = 0.0d;
                        if(procPredMatrix[p][q][r][s] == null)
                            dblVal = 0.0d;
                        else
                            dblVal = procPredMatrix[p][q][r][s];
                        Complex complxVal = new Complex(dblVal);
                        Complex stftIndexVal = ((Complex[][]) stftValues.get(s))[pqInd][r];
                        Complex[] var10000 = procPred3DMatrix[pqInd][r];
                        Complex var10002 = complxVal.multiply(stftIndexVal);
                        Intrinsics.checkNotNullExpressionValue(var10002, "complxVal.multiply(stftIndexVal)");
                        var10000[s] = var10002;
                    }

                    if (breakFlag) {
                        break;
                    }
                }

                if (breakFlag) {
                    break;
                }
            }

            if (breakFlag) {
                break;
            }
        }

        return procPred3DMatrix;
    }

    private final Complex multiplyComplexNumbers(Complex complxVal1, Complex complxVal2) {
        double realVal = complxVal1.getReal() * complxVal2.getReal() - complxVal1.getImaginary() * complxVal2.getImaginary();
        double imgVal = complxVal1.getReal() * complxVal2.getImaginary() + complxVal1.getImaginary() * complxVal2.getReal();
        return new Complex(realVal, imgVal);
    }

    private final Double[][][][] gen4DMatrixFromModelOutput(TensorBuffer valTensorBuffer, int[] outputShape) {
        int i = outputShape[0];
        Double[][][][] var5 = new Double[i][][][];

        int j;
        int var9;
        int var11;
        for (j = 0; j < i; ++j) {
            boolean var8 = false;
            var9 = outputShape[1];
            Double[][][] var10 = new Double[var9][][];

            for (var11 = 0; var11 < var9; ++var11) {
                boolean var15 = false;
                int var16 = outputShape[2];
                Double[][] var17 = new Double[var16][];

                for (int var18 = 0; var18 < var16; ++var18) {
                    boolean var22 = false;
                    Double[] var23 = new Double[outputShape[3]];
                    var17[var18] = var23;
                }

                Double[][] var24 = (Double[][]) var17;
                var10[var11] = var24;
            }

            Double[][][] var27 = (Double[][][]) var10;
            var5[j] = var27;
        }

        Double[][][][] outputMatrixValue = (Double[][][][]) var5;
        i = 0;

        for (int var28 = outputShape[0]; i < var28; ++i) {
            j = 0;

            for (int var7 = outputShape[1]; j < var7; ++j) {
                int k = 0;

                for (var9 = outputShape[2]; k < var9; ++k) {
                    int l = 0;

                    for (var11 = outputShape[3]; l < var11; ++l) {
                        int indexVal = i * outputShape[1] + j * outputShape[2] + k * outputShape[3] + l;
                        outputMatrixValue[i][j][k][l] = valTensorBuffer.getBuffer().getDouble(indexVal);
                    }
                }
            }
        }

        return outputMatrixValue;
    }

    private final Double[] genDoubleArray(Double[][] valInpArray) {
        int arrIndex1 = ((Object[]) valInpArray).length;
        int arrIndex2 = valInpArray[0].length;
        int DoubleArraySize = arrIndex1 * arrIndex2;
        Double[] valDoubleArray = new Double[DoubleArraySize];
        int i = 0;

        for (int var7 = arrIndex1; i < var7; ++i) {
            int j = 0;

            for (int var9 = arrIndex2; j < var9; ++j) {
                int arrIndexVal = i * arrIndex2 + j;
                Double var10002 = valInpArray[i][j];
                Intrinsics.checkNotNull(valInpArray[i][j]);
                valDoubleArray[arrIndexVal] = var10002;
            }
        }

        return valDoubleArray;
    }

    private final ArrayList _stft(float[][] stereoMatrix) {
        boolean N = true;
        boolean H = true;
        int sampleRate = '걄';
        ArrayList stftValuesList = new ArrayList();
        int i = 0;

        for (int var7 = stereoMatrix[0].length; i < var7; ++i) {
            float[] doubleStream = this.getColumnFromMatrix(stereoMatrix, i);
            JLibrosa var10000 = this.jLibrosa;
            Intrinsics.checkNotNull(var10000);
            Complex[][] stftComplexValues = var10000.generateSTFTFeaturesWithPadOption(doubleStream, sampleRate, 40, 4096, 128, 1024, false);
            Intrinsics.checkNotNullExpressionValue(stftComplexValues, "stftComplexValues");
            Complex[][] transposedSTFTComplexValues = this.transposeMatrix(stftComplexValues);
            stftValuesList.add(transposedSTFTComplexValues);
        }

        return stftValuesList;
    }

    private final float[][][][] processSTFTValues(ArrayList stftValuesList) {
        int segmentLen = 512;
        float[][][] stft3DMatrixValues = this.gen3DMatrixFrom2D(stftValuesList, segmentLen);
        int splitValue = ((stft3DMatrixValues).length + segmentLen - 1) / segmentLen;
        return this.gen4DMatrixFrom3D(stft3DMatrixValues, splitValue, segmentLen);
    }

    private final float[][][][] gen4DMatrixFrom3D(float[][][] stft3DMatrixValues, int splitValue, int segmentLen) {
        int yVal = 1024;
        int zVal = stft3DMatrixValues[0][0].length;
        float[][][][] var7 = new float[splitValue][][][];

        int var8;
        int retInd;
        for (var8 = 0; var8 < splitValue; ++var8) {
            boolean var10 = false;
            retInd = segmentLen;
            float[][][] var12 = new float[segmentLen][][];

            for (int var13 = 0; var13 < retInd; ++var13) {
                boolean var17 = false;
                short var18 = (short) yVal;
                float[][] var19 = new float[yVal][];

                for (int var20 = 0; var20 < var18; ++var20) {
                    boolean var24 = false;
                    float[] var25 = new float[zVal];
                    var19[var20] = var25;
                }

                float[][] var26 = var19;
                var12[var13] = var26;
            }

            float[][][] var29 =  var12;
            var7[var8] = var29;
        }

        float[][][][] stft4DMatrixValues =  var7;
        int p = 0;

        for (var8 = splitValue; p < var8; ++p) {
            int q = 0;

            for (int var31 = segmentLen; q < var31; ++q) {
                retInd = p * segmentLen + q;
                int r = 0;

                for (short var33 = (short) yVal; r < var33; ++r) {
                    int s = 0;

                    for (int var15 = zVal; s < var15; ++s) {
                        if(stft3DMatrixValues[retInd][r][s] != 0.0)
                        stft4DMatrixValues[p][q][r][s] =  stft3DMatrixValues[retInd][r][s];
                        else
                            stft4DMatrixValues[p][q][r][s] = 0.0F;
                    }
                }
            }
        }

        return stft4DMatrixValues;
    }

    private final float[][][] gen3DMatrixFrom2D(ArrayList mat2DValuesList, int segmentLen) {
        int padSize = this.computePadSize(((Object[]) mat2DValuesList.get(0)).length, segmentLen);
        int matrixXLen = ((Object[]) mat2DValuesList.get(0)).length + padSize;
        float[][][] var6 = new float[matrixXLen][][];

        int var7;
        int var10;
        int var12;
        for (var7 = 0; var7 < matrixXLen; ++var7) {
            boolean var9 = false;
            var10 = ((Complex[][]) mat2DValuesList.get(0))[0].length;
            float[][] var11 = new float[var10][];

            for (var12 = 0; var12 < var10; ++var12) {
                boolean var16 = false;
                float[] var17 = new float[mat2DValuesList.size()];
                var11[var12] = var17;
            }

            float[][] var20 =  var11;
            var6[var7] = var20;
        }

        float[][][] complex3DMatrix = var6;
        int k = 0;

        for (var7 = mat2DValuesList.size(); k < var7; ++k) {
            Object var10000 = mat2DValuesList.get(k);
            Intrinsics.checkNotNullExpressionValue(var10000, "mat2DValuesList[k]");
            Complex[][] mat2DValues = (Complex[][]) var10000;
            int i = 0;

            for (var10 = matrixXLen; i < var10; ++i) {
                int j = 0;

                for (var12 = mat2DValues[0].length; j < var12; ++j) {
                    float value = 0.0F;
                    if (i < ((Object[]) mat2DValues).length) {
                        if(mat2DValues[i][j] != null)
                            value = (float) mat2DValues[i][j].abs();
                        else
                            value = 0.0F;
                    }

                    complex3DMatrix[i][j][k] = value;
                }
            }
        }

        return complex3DMatrix;
    }

    private final int computePadSize(int currentMatrixLen, int segmentLen) {
        int tensorSize = currentMatrixLen % segmentLen;
        return segmentLen - tensorSize;
    }

    private final float[] getColumnFromMatrix(final float[][] DoubleMatrix, final int column) {
        double[] var10000 = IntStream.range(0, ((Object[]) DoubleMatrix).length).mapToDouble((IntToDoubleFunction) (new IntToDoubleFunction() {
            public final double applyAsDouble(int i) {
                return (double) DoubleMatrix[i][column];
            }
        })).toArray();
        Intrinsics.checkNotNullExpressionValue(var10000, "IntStream.range(0, Double…n].toDouble()}).toArray()");
        double[] doubleStream = var10000;
        float[] DoubleArray = new float[doubleStream.length];
        int i = 0;

        for (int var6 = doubleStream.length; i < var6; ++i) {
            DoubleArray[i] = (float) doubleStream[i];
        }

        return DoubleArray;
    }

    @NotNull
    public final Complex[][] transposeMatrix(@NotNull Complex[][] matrix) {
        Intrinsics.checkNotNullParameter(matrix, "matrix");
        int m = ((Object[]) matrix).length;
        int n = matrix[0].length;
        Complex[][] var5 = new Complex[n][];

        int var6;
        for (var6 = 0; var6 < n; ++var6) {
            boolean var8 = false;
            Complex[] var11 = new Complex[m];
            var5[var6] = var11;
        }

        Complex[][] transposedMatrix = (Complex[][]) var5;
        int x = 0;

        for (var6 = n; x < var6; ++x) {
            int y = 0;

            for (int var13 = m; y < var13; ++y) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }

    @NotNull
    public final float[][] transposeMatrix(@NotNull float[][] matrix) {
        Intrinsics.checkNotNullParameter(matrix, "matrix");
        int m = ((Object[]) matrix).length;
        int n = matrix[0].length;
        float[][] var5 = new float[n][];

        int var6;
        for (var6 = 0; var6 < n; ++var6) {
            boolean var8 = false;
            float[] var11 = new float[m];
            var5[var6] = var11;
        }

        float[][] transposedMatrix = (float[][]) var5;
        int x = 0;

        for (var6 = n; x < var6; ++x) {
            int y = 0;

            for (int var13 = m; y < var13; ++y) {
                transposedMatrix[x][y] = matrix[y][x];
            }
        }

        return transposedMatrix;
    }



    public File convertToWav(String url) throws EncoderException, IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/doKaraokeAI/");
        String audioName = URLUtil.guessFileName(url, null, null);
        audioName = audioName.replace(" ","");
        audioName = audioName.replaceAll("[^a-zA-Z0-9\\.\\-\\_]", "_");
        File source = new File(url);
        File targetFile = null;
        if (source.exists()){
            String finalFileName;
            finalFileName = audioName.replace(audioName.substring(audioName.lastIndexOf(".")),".wav");
            targetFile = new File(path, finalFileName);
            if(targetFile.exists()){
                targetFile.delete();
            }


            Uri outUri = Uri.fromFile(targetFile);
            targetFile = new File(outUri.getPath());


            final Integer bitRate = 16;
            final Integer samplingRate = 44100;//8000;
            final Integer channels = 2;
            final String codec = "pcm_s16le";
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec(codec);
            audio.setChannels(channels);
            audio.setBitRate(bitRate);
            //  audio.setVolume(400);
            audio.setSamplingRate(samplingRate);
            Encoder encoder = new Encoder(new MyDefaultFFMPEGLocator());
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);


            try {
                encoder.encode(new MultimediaObject(source), targetFile, attrs);
            } catch (EncoderException e) {
                e.printStackTrace();
            }

        }


        return targetFile;
    }


    @Override
    protected void onPreExecute() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, "Creating Karaoke Track...", Toast.LENGTH_SHORT);
                toast.show();
            }
        });


        ShowProgress progress = new ShowProgress(dialog, "Karaoke Track ...");
        progress.execute();

        super.onPreExecute();
    }



    @Override
    protected Object doInBackground(Object... objects) {
        if(FFMPEG.getInstance(context).isSupported()) {
            try {
                File sourceFile = convertToWav(url);
                processAudioSeparation(sourceFile.getAbsolutePath(), context);
            } catch (FileFormatNotSupportedException | IOException | WavFileException | EncoderException e) {
                e.printStackTrace();
            }
            System.out.print(10000);
        }


        MainActivity2.changeActivity(context);

        return "null";
    }



    @Override
    protected void onPostExecute(Object o) {
        ShowProgress.dialogDismiss();
        cancel(true);
        Uri outUri = Uri.fromFile(targetFile);
        MusicPlayer.SoundPlayer(context,outUri);
        MusicPlayer.player.start();
        MainFragment.isPlaying = true;

        new Handler(Looper.getMainLooper()).post(() -> {
            Toast toast = Toast.makeText(context, "Karaoke Track created!", Toast.LENGTH_SHORT);
            toast.show();
        });

        super.onPostExecute(o);
    }



    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
    }



    @Override
    protected void onCancelled() {
        try {
            handleCancelMethod();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleCancelMethod() throws IOException {
        cancel(true);
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast toast = Toast.makeText(context, "Karaoke Track creation Cancelled!", Toast.LENGTH_SHORT);
            toast.show();
        });
    }

}
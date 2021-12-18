package com.stt.project.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.threeten.bp.Duration;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.TimedRetryAlgorithm;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.cloud.speech.v1.WordInfo;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.StorageOptions;
import com.google.protobuf.ByteString;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;


/**
 * ApiService
 * 
 * @author hj.jeon
 * 
 * @description
 *
 */
@Service
public class ApiSeivice {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	 * // 음성파일 -> 텍스트 변환 public Map<String,Object> chgSphToTxt(MultipartFile file) {
	 * 
	 * logger.debug(" @@ chgSphToTxt call in ApiSerivice ==> ");
	 * 
	 * // 결과값 리턴을 위한 객체 Map<String, Object> resultMap = new HashMap<String,
	 * Object>();
	 * 
	 * try { // 폴링 알고리즘 구성 SpeechSettings.Builder speechSettings =
	 * SpeechSettings.newBuilder(); TimedRetryAlgorithm timedRetryAlgorithm =
	 * OperationTimedPollAlgorithm.create(RetrySettings.newBuilder()
	 * .setInitialRetryDelay(Duration.ofMillis(500L)).setRetryDelayMultiplier(1.5)
	 * .setMaxRetryDelay(Duration.ofMillis(5000L)).setInitialRpcTimeout(Duration.
	 * ZERO) .setRpcTimeoutMultiplier(1.0) .setMaxRpcTimeout(Duration.ZERO)
	 * .setTotalTimeout(Duration.ofHours(24L)) .build());
	 * speechSettings.longRunningRecognizeOperationSettings().setPollingAlgorithm(
	 * timedRetryAlgorithm);
	 * 
	 * try (SpeechClient speechClient = SpeechClient.create(speechSettings.build()))
	 * {
	 * 
	 * // 변환할 오디오파일 경로 지정 String filePath = "gs://stotbk/uploads/nine_minute.wav";
	 * 
	 * // 요청 설정 - 사용 목적에 맞게 변 RecognitionConfig config =
	 * RecognitionConfig.newBuilder() .setEncoding(AudioEncoding.LINEAR16) //
	 * .setSampleRateHertz(16000) .setSampleRateHertz(44100)
	 * .setAudioChannelCount(2) .setEnableWordTimeOffsets(true) //
	 * .setLanguageCode("en-US") .setLanguageCode("ko-KR") .build();
	 * 
	 * RecognitionAudio audio = RecognitionAudio.newBuilder() .setUri(filePath)
	 * .build();
	 * 
	 * // 음성인식 처리 // 1분 미만 동기형 처리 // RecognizeResponse response =
	 * speechClient.recognize(config, audio); // Non-Blocking 으로 호출을 하며 긴 파일 일때는
	 * LongRunningRecognizeResponse를 사용함
	 * OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata>
	 * response = speechClient.longRunningRecognizeAsync(config, audio);
	 * 
	 * while (!response.isDone()) { logger.debug("Waiting for response...");
	 * Thread.sleep(10000); }
	 * 
	 * // 1분 미만 // List<SpeechRecognitionResult> results =
	 * response.getResultsList(); // 1분 이상 List<SpeechRecognitionResult> results =
	 * response.get().getResultsList();
	 * 
	 * // 번역되는 텍스트 담기 StringBuffer sb = new StringBuffer();
	 * 
	 * for (SpeechRecognitionResult result : results) { SpeechRecognitionAlternative
	 * alternative = result.getAlternativesList().get(0); //
	 * sb.append(alternative.getTranscript());
	 * 
	 * int cnt = 0;
	 * 
	 * for (WordInfo wordInfo : alternative.getWordsList()) { if (cnt == 0) {
	 * sb.append(wordInfo.getStartTime().getSeconds()); sb.append(".");
	 * sb.append(wordInfo.getStartTime().getNanos() / 100000000); sb.append(" : ");
	 * sb.append(wordInfo.getWord()); sb.append(" "); cnt++; } else if (cnt > 0 &&
	 * cnt <= 8) { sb.append(wordInfo.getWord()); sb.append(" "); cnt++; if (cnt ==
	 * 8) { sb.append("\n"); cnt = 0; } } } }
	 * 
	 * resultMap.put("result", sb); }
	 * 
	 * // 업로드파일 제거 // newFile.deleteOnExit(); } catch (Exception e) {
	 * e.printStackTrace(); resultMap.put("result","fail"); }
	 * 
	 * return resultMap; }
	 */
	
	public void AmrToWav(String fileName) {
		Runtime rt = Runtime.getRuntime();
		Process pc = null;
		try {
			String ffmpegBin = "src/main/resources/bin/ffmpeg.exe";
			String input = fileName;
			pc = rt.exec(ffmpegBin+"-i "+ "input.amr" + " -ar 44100 "+"output.wav");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}  
	// GCS File Upload
	public String uploadToGcs(MultipartFile file) throws Exception  {
		
		logger.debug(" @@ uploadToGcs call in ApiSerivice ==> ");

		String filePath = "";
		
		try {	
			logger.debug(" @@ File Upload Start ==> ");
			
			// Key 파일
			String keyFileName = "stot-331612-a9ab377e273e.json";
			InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();
			
		    // Key 파일 등록
			Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build().getService();
			
			BlobInfo blobInfo = storage.create(
				BlobInfo.newBuilder("stotbk", "uploads/" + file.getOriginalFilename()).build(), //get original file name
				file.getBytes(), // the file
				BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ) // Set file permission
			);
			
			
			if(file.getOriginalFilename().contains(".amr")) {
				AmrToWav(file.getOriginalFilename());
			}
			
			filePath = "gs://stotbk/uploads/" + file.getOriginalFilename(); // 업로드된 파일 url 리턴 
			logger.debug(" @@ File Upload End ==> " + filePath);
			
		}catch(IllegalStateException e){
			throw new RuntimeException(e);
		}
		
		return filePath;
	}
	
	
	// 음성파일 -> 텍스트로 변환 
	public Map<String, Object> transcribeDiarization(String filePath) throws Exception {
		
		logger.debug(" @@ transcribeDiarization call in ApiSerivice ==> ");
		
		// 반환 객체 지정
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> msgLog = new ArrayList<String>();
		List<String> startTime = new ArrayList<String>();
		List<String> speakerNo = new ArrayList<String>();
		List<String> text = new ArrayList<String>();
		
		try (SpeechClient speechClient = SpeechClient.create()) {
			// 파일 가져오기 
			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
//											   .setContent(ByteString.copyFrom(content))
											   .setUri(filePath)
											   .build();

			SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
																.setEnableSpeakerDiarization(true)
																.setMinSpeakerCount(2) // 최소 인원수
																.setMaxSpeakerCount(10) // 최대 인원수
																.build();

			// Configure request to enable Speaker diarization
			RecognitionConfig config = RecognitionConfig.newBuilder()
									  .setEncoding(AudioEncoding.LINEAR16)
									  .setLanguageCode("en-US")
									  .setAudioChannelCount(2)
									  .setDiarizationConfig(speakerDiarizationConfig)
									  .build();

			// Perform the transcription request
//			RecognizeResponse recognizeResponse = speechClient.recognize(config, recognitionAudio);

			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
            		speechClient.longRunningRecognizeAsync(config, recognitionAudio);
			
			while (!response.isDone()) {
				logger.debug("Waiting for response...");
				Thread.sleep(10000);
			}
			
			// Speaker Tags are only included in the last result object, which has only one
			// alternative.
			LongRunningRecognizeResponse longRunningRecognizeResponse = response.get();
			SpeechRecognitionAlternative alternative = longRunningRecognizeResponse
					.getResults(longRunningRecognizeResponse.getResultsCount() - 1).getAlternatives(0);

			// The alternative is made up of WordInfo objects that contain the speaker_tag.
			WordInfo wordInfo = alternative.getWords(0);
			int currentSpeakerTag = wordInfo.getSpeakerTag();

			// 전체 내용 텍스트로 저장
			StringBuilder speakerWords = new StringBuilder(String.format("%d.%d Sec - Speaker %d: %s",
					wordInfo.getStartTime().getSeconds(), wordInfo.getStartTime().getNanos() / 100000000,
					wordInfo.getSpeakerTag(), wordInfo.getWord()));
			
			// 대화내용만 텍스트 저장 
			StringBuilder textWords = new StringBuilder(wordInfo.getWord());

			// 첫번째 대화시작시간 저장 
			startTime.add(wordInfo.getStartTime().getSeconds() + "." + wordInfo.getStartTime().getNanos() / 100000000);
			
			for (int i = 1; i < alternative.getWordsCount(); i++) {
				wordInfo = alternative.getWords(i);
				if (currentSpeakerTag == wordInfo.getSpeakerTag()) {
					speakerWords.append(" ");
					speakerWords.append(wordInfo.getWord());
					textWords.append(" ");
					textWords.append(wordInfo.getWord());
					// 마지막 화자가 말하는 내용은 바로 입력하도록 처리
					if(i==alternative.getWordsCount()-1) {
						// 전체 메시지 리스트에 추가 
						msgLog.add(speakerWords.toString());
						// 스피커 번호 리스트에 추가 
						speakerNo.add(String.valueOf(currentSpeakerTag));
						// 대화내용 리스트에 추가 
						text.add(textWords.toString());
					}
				} else {
					// 전체 메시지 리스트에 추가 
					msgLog.add(speakerWords.toString());
					// 스피커 번호 리스트에 추가 
					speakerNo.add(String.valueOf(currentSpeakerTag));
					// 대화내용 리스트에 추가 
					text.add(textWords.toString());
					// 다음 대화내용을 받기 위해 초기화 
					speakerWords.setLength(0);
					textWords.setLength(0);
					
					// 다음 대화내용 기록 시작
					speakerWords.append(String.format("%d.%d Sec - Speaker %d: %s",
								wordInfo.getStartTime().getSeconds(), wordInfo.getStartTime()
								.getNanos() / 100000000,
								wordInfo.getSpeakerTag(), wordInfo.getWord()));
					textWords = new StringBuilder(wordInfo.getWord());
					
					// 다음 스피커 번호 세팅 
					currentSpeakerTag = wordInfo.getSpeakerTag();
					
					// 대화시작시간 저장 
					startTime.add(wordInfo.getStartTime().getSeconds() + "." + wordInfo.getStartTime()
								.getNanos() / 100000000);
						
						
				}
			}

			resultMap.put("msg_log", msgLog);
			resultMap.put("start_time", startTime);
			resultMap.put("speaker_no", speakerNo);
			resultMap.put("text", text);
			
			return resultMap;
		}
	}
	
	// GCS File Upload
		public void removeFileFromGcs(MultipartFile file) throws Exception  {
			
			logger.debug(" @@ removeFileIntoGcs call in ApiSerivice ==> ");

			String filePath = "";
			
			try {	
				logger.debug(" @@ File Delete Start ==> ");
				
				// Key 파일
				String keyFileName = "stot-331612-a9ab377e273e.json";
				InputStream keyFile = ResourceUtils.getURL("classpath:" + keyFileName).openStream();
				
			    // Key 파일 등록
				Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).setProjectId("StoT").build().getService();
				storage.delete("stotbk", "uploads/" + file.getOriginalFilename());
				
				logger.debug(" @@ File Delete End ==> ");
				
			}catch(IllegalStateException e){
				throw new RuntimeException(e);
			}
		}
}

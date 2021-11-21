package com.stt.project.controller;

import java.util.HashMap;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stt.project.service.ApiSeivice;


/**
 * ApiController
 * 
 * @author hj.jeon
 * 
 * @description
 *
 */

@RequestMapping(value = "/api")
@RestController
public class ApiController {

	@Autowired
	private ApiSeivice apiService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 음성파일 -> 텍스트변환 API
	@RequestMapping(value = "/chgSphToTxt", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> chgSphToTxt(@RequestPart(value="file", required=false) MultipartFile file) {
		
		logger.debug(" @@ chgSphToTxt call in apiController ==> ");
		
		// 결과값 리턴을 위한 객체
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
//		resultMap = apiService.chgSphToTxt(file);
		try {
			// 변환할 음성 파일 업로드 
			String filePath = apiService.uploadToGcs(file);
			
			// 음성파일 -> 텍스트 변환 
			resultMap = apiService.transcribeDiarization(filePath);
			
			// 변환작업 끝난 파일 삭제
			apiService.removeFileFromGcs(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resultMap;
	}
}

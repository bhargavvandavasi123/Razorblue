package com.pageinfo.controller;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class SignInController {
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private IDataReadLayer  dataReadLayer ;
	
	@GetMapping(path = "SignIn", 
	        consumes = MediaType.APPLICATION_JSON_VALUE, 
	        produces = MediaType.APPLICATION_JSON_VALUE)
	public String getSignInResponse() {
		SignInRequest ref = new SignInRequest();
		ref.setPassword("pageinfoenappsys");
		ref.setUsername("EnAppSys2022!");
        return restTemplate.postForEntity("https://api.apis.guru/v2/list.json", ref , SignInResponse.class).getBody().getToken();    
	}
	
	@GetMapping(path = "/getChartData/{chartCode}")
    public void getChartData(@PathVariable("chartCode") String chartCode) {
//	    final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
//		DataType.populate(credentialsProvider);
		 String url = "https://appqa.enappsys.com/pages/getSetup?code=" + chartCode;

		
//		restTemplate.getForEntity(url, null, null)
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
       
           
            HttpUriRequest request = RequestBuilder.get(url)
                    .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getSignInResponse())
                    .build();

            CloseableHttpResponse response = httpClient.execute(request);

            String responseString = EntityUtils.toString(response.getEntity());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            PageData pageData = objectMapper.readValue(responseString, PageData.class);

            System.out.println("Type: " + pageData.getType());
            System.out.println("Permissions: " + pageData.getPermissions());
           
          
            
            for (ChartItem chartItem : pageData.getChartItems()) {
                System.out.println("ChartItem:");
                System.out.println("Type: " + chartItem.getType());
                System.out.println("Entity: " + chartItem.getEntity());
                System.out.println("Name: " + chartItem.getName());
                System.out.println("MinAvMax: " + chartItem.getMinavmax());
                
                
                DataType dataType = DataType.getByGUICode(chartItem.getType());
                System.out.println(dataType.getCode());
                System.out.println(dataType.getBaseResolution());
                
                IDataReadLayer dataReadLayer = DataReadLayerFactory.createDataReadLayer(credentialsProvider);
                
                dataReadLayer.getEntityInformation(pageData.getType() "begin with");
                
                
                
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


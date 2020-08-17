package com.synergy.socdoc.reservation.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.HashMap;
import java.util.List;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.synergy.socdoc.common.MyUtil;
import com.synergy.socdoc.member.HpInfoVO;
import com.synergy.socdoc.member.MemberVO;
import com.synergy.socdoc.reservation.service.InterReserveService;

@Component
@Controller
public class ReservationController {

   
    @Autowired
      private InterReserveService service;
    
    
      // === 병원 List 보여주기 요청페이지 === // 
      @RequestMapping(value="/reserve.sd")
      public ModelAndView hpinfovoList(HttpServletRequest request,ModelAndView mav) {
       
      // 병원 진료과목 가져오기
            List<String> deptIdList = service.deptIdList(); //병원 진료과목 받아오기

            mav.addObject("deptIdList", deptIdList);
            List<HpInfoVO> hpinfovoList  = null; // 병원목록 받아오기
            
            
            String sDeptIdes = request.getParameter("sDeptIdes"); // 진료과목
            
			String searchType = request.getParameter("searchType"); 
			String searchWord = request.getParameter("searchWord");
			String str_currentShowPageNo = request.getParameter("currentShowPageNo");
            // sDeptIdes ==> "-9999,50,110"
            // sDeptIdes ==> ""
            // sDeptIdes ==> "30,80,90,110"
            
			if(searchWord == null || searchWord.trim().isEmpty()) {
				searchWord = "";
			}
			
			if(searchType == null) {
				searchType = "";
			}
            
            HashMap<String, Object> paraMap = new HashMap<>();
            paraMap.put("searchType", searchType);
			paraMap.put("searchWord", searchWord);
            
            if(sDeptIdes !=null && !"".equals(sDeptIdes)) {
               String[] deptIdArr = sDeptIdes.split(",");
 
               paraMap.put("deptIdArr", deptIdArr);
               
               mav.addObject("sDeptIdes", sDeptIdes);
               // 뷰단에서 검색 버튼 클릭시 체크되어진 값을 유지시키기 위한 것이다.
              
            } 
            
			System.out.println(searchWord);
            
            // 병원 목록 보여주기 List<HashMap<String,String>> hpinfovoList  = service.hpinfovoList(paraMap);

			/////////////////페이징 처리///////////////////////
			   
				
				

			
				
				int totalCount = 0; //총 게시물 건수
				int sizePerPage	= 6;//한 페이지당 보여줄 게시물 건수
				int currentShowPageNo = 0;//현재 보여주는 페이지 번호로서, 초기치로는 1페이지로 설정한다
				int totalPage = 0;//총 페이지 수(웹브라우저상에 보여줄 총 페이지 개수,페이지바)
				
				int startRno = 0;//시작 행번호
				int endRno = 0;//끝 행번호
				
				// 먼저 총 게시물 건수(totalCount)를 구해와야한다
							// 총 게시물 건수(totalCount)는 검색조건이 있을 때와 없을때로 나누어진다
				
				totalCount = service.getTotalCount(paraMap);
				System.out.println("totalCount :"+totalCount);
				
				//만약에 총 게시물 건수(totalCount)가 127개 이라면
				//총 페이지 수(totalPage)는 13개가 되어야 한다.
				totalPage = (int) Math.ceil ((double)totalCount/sizePerPage); 
				// (double)127/10 ==> 12.7 ==> Math.ceil(12.7) ==> (int)13.0 0아니면 얘보다 크게나온다
				// (double)120/10 ==> 12.0 ==> Math.ceil(12.0) ==> (int)12.0
				
				if(str_currentShowPageNo == null) {
					//게시판에 보여지는 초기화면
					
					currentShowPageNo =1;
					// 즉,초기화면인/list.action은 /list.action?currentShowPageNo=1로 하겠다는 말이다
				}
				else {
					try {
						currentShowPageNo = Integer.parseInt(str_currentShowPageNo);
						//if(currentShowPageNo< 1 || currentShowPageNo> totalPage) {
						if(currentShowPageNo<=0 || currentShowPageNo> totalPage) {
							currentShowPageNo =1;
						}
					}catch(NumberFormatException e) {
						currentShowPageNo = 1;
					}
				}
				
				startRno = ((currentShowPageNo - 1 ) * sizePerPage) + 1;
				endRno = startRno + sizePerPage - 1; 
			
			
				paraMap.put("startRno",String.valueOf(startRno)); //startRno은 string타입이안맞기 때문에 올수없다(곱하기,빼기) String으로바꿔야한다
				paraMap.put("endRno",String.valueOf(endRno));
				
				hpinfovoList = service.searchwWithPaging(paraMap);
				
				// 페이징 처리한 글목록 가져오기(검색이 있든지, 검색이 없든지 모두 다 포함한것)
				
				if(!"".equals(searchWord)) {
					mav.addObject("paraMap",paraMap);
				}
				// == #119.페이지바 만들기 ==
				String pageBar = "<ul style='list-style: none;'>";
				
				int blockSize = 5;
				//blockSize 는 1개블럭(토막)당 보여지는 페이지번호의 개수이다.
				
				  /* 1 2 3 4 5 6 7 8 9 10 다음 			 --1개블럭
				 이전 11 12 13 14 15 16 17 18 19 20 다음 --1개블럭
				 이전 21 22 23*/
				 
				
				int loop = 1;
				
				 	//loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[지금은 10개(==blockSize)까지만 증가하는 용도]
				 
				int pageNo = ((currentShowPageNo - 1)/blockSize) * blockSize + 1;
				// *** !! 공식이다. !! *** //
				
			
				
				String url = "reserve.sd";//URL어느페이지로 가겠습니까? get방식이기때문에 뒤에?붙을수있다
				
				// ===[이전] 만들기 ===
				if(pageNo != 1) {//첫번째페이지가아니라 두번째페이지부터 이전을 만드렁라
				pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+(pageNo-1)+"'>[이전]</a></li>";
				}
				while(!(loop > blockSize || pageNo > totalPage)) {//while문을 빠져나가는 좆건문
					
					//1 2 3 4 5 6 7 8 9 10 그다음에 11이되면 blockSize와 비교하고 빠져나간다
					//24가 끝이라 totalpage보다 크면 빠져나간다
					if(pageNo == currentShowPageNo) {//현재보고자하는 페이지넘버가
						pageBar += "<li style='display:inline-block; width:20px; font-size:12pt; color:pink; padding:2px 4px;'>"+pageNo+"</li>";
					}else {
						pageBar += "<li style='display:inline-block; width:20px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>"+pageNo+"</a></li>";
					}
					
					loop++;
					pageNo++;
				}//end while
				
				// ===[다음] 만들기 ===
				if(!(pageNo > totalPage)) {//맨마지막껄로 빠져나온게 아니라면
				pageBar += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='"+url+"?searchType="+searchType+"&searchWord="+searchWord+"&currentShowPageNo="+pageNo+"'>[다음]</a></li>";
				}
				pageBar += "</ul>";
				
				mav.addObject("pageBar",pageBar);//request 영역에 담는다
				
				String gobackURL = MyUtil.getCurrentURL(request);
				// #121.페이징 처리되어진 후 특정글제목을 클릭하여 상세내용을 본 이후
				// 사용자가 목록보기 버튼을 클릭했을때 돌아갈 페이지를 알려주기 위해
				// 현재 페이지 주소를 뷰단으로 넘겨준다.
				mav.addObject("gobackURL", gobackURL);
				//System.out.println("~~~~~확인용 gobackURL:"+gobackURL);	
				
				////////////////페이징 처리 끝 /////////////////////
  
            
            
            HttpSession session = request.getSession();
               
               
               MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
                  if(loginuser != null) {
                  String userid = loginuser.getUserid();
               
                  mav.addObject("userid",userid);
                  MemberVO membervo = service.viewMyinfo(userid);
                  mav.addObject("membervo", membervo);
                  }
                  

            mav.addObject("hpinfovoList", hpinfovoList);
            mav.setViewName("Reservation/reservation.tiles1");
            
            return mav;
         }
        

      @ResponseBody
      @RequestMapping(value="/ajax/getNumOfReserv.sd")
      public String getNumOfReserv(HttpServletRequest request, HttpServletResponse response) {
    	  String visitDate = request.getParameter("visitDate");
    	  String hpSeq = request.getParameter("hpSeq");
    	  int day = Integer.parseInt(request.getParameter("day"));
    	  
    	  HashMap<String, String> paraMap = new HashMap<>();
    	  
    	  paraMap.put("visitDate", visitDate);
    	  paraMap.put("hpSeq", hpSeq);
    	  
          // 특정 병원의 예약이 가능한 시간대만 가져옴 
    	  List<String> hours = service.getHours(paraMap);	// ["9:00", "10:00" ,...]
    	  
    	  JsonObject json = new JsonObject();
    	  JsonArray hoursArr = new JsonArray();
    	  for(int i=0;i<hours.size();i++) {
    		  hoursArr.add(hours.get(i));
    	  }
    	  
    	  json.add("hours", hoursArr);
    	  
    	  // 특정 병원의 영업시간 가져오기
    	  List<HashMap<String, String>> openHours = service.getOpeningHours(hpSeq); //[ {open: 1, close: 10}, {open: 2, close: 7}, ... ]
    	  if(openHours != null && openHours.get(day) != null) {
    		  JsonObject obj = new JsonObject();
    		  obj.addProperty("open", openHours.get(day).get("open"));			
    		  obj.addProperty("close", openHours.get(day).get("close"));		
    		  json.add("openHours", obj);
  		}
    	  
         return json.toString();
      }
      
      // === 예약자명 정보 보여주기 요청페이지 === // 
      
      
      
      
      // === 예약완료 페이지 요청 === // 
      @RequestMapping(value="/reserve_result.sd")
      public String reserve_result() {
         return "Reservation/reservation_result.tiles1";
      }
}
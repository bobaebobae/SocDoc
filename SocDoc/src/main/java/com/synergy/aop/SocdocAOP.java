package com.synergy.aop;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synergy.socdoc.admin.InterAdminService;
import com.synergy.socdoc.common.MyUtil;
import com.synergy.socdoc.member.HpInfoVO;
import com.synergy.socdoc.member.HpMemberVO;
import com.synergy.socdoc.hpMem.InterHpMemService;
import com.synergy.socdoc.member.*;

@Aspect
@Component
public class SocdocAOP {

	@Autowired
	InterHpMemService service;

	@Autowired
	InterAdminService adminService;

	@Pointcut("execution(public * com.synergy..*Controller.requiredLogin_*(..) )")
	public void requiredLogin() {
	}

	// == Before Advice(공통관심사, 보조업무)를 구현한다.
	@Before("requiredLogin()") // (pointcut)메소드가 작동하기 전에 코딩되어지는 메소드를 수행해라
	public void loginChk(JoinPoint joinPoint) { // 로그인 유무 검사 메소드 작성하기

		// 첫번째 파라미터는 HttpServletRequest이므로 리턴타입이 object니 캐스팅해준다
		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
		HttpServletResponse response = (HttpServletResponse) joinPoint.getArgs()[1];

		System.out.println("controller의 메소드가 실행되기 전, HpMemAOP에서 정의한 메소드가 실행됩니다.");
		System.out.println("현재 servletContext : " + request.getServletContext());

		HttpSession session = request.getSession();
		MemberVO member = (MemberVO) session.getAttribute("loginuser");

		String msg = "로그인이 필요한 페이지입니다!";
		String loc = "";

		if (member == null) {

			loc = request.getContextPath() + "/login.sd";
			request.setAttribute("msg", msg);
			request.setAttribute("loc", loc);

			// === 현재 페이지의 주소(URL)알아오기 ===
			String url = MyUtil.getCurrentURL(request);

			if (url.endsWith("?null")) {
				url = url.substring(0, url.indexOf("?")); // 처음부터 ?앞까지
			}

			System.out.println(url);
			session.setAttribute("gobackURL", url); // 세션에 url정보를 저장시켜 둔다.

			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp");

			try {
				dispatcher.forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}

		}

	}

	@Pointcut("execution(public * com.synergy..*Controller.requiredLoginHp_*(..) )")
	public void requiredLoginHp() {
	}

	@Before("requiredLoginHp()")
	public void loginChkHp(JoinPoint joinPoint) {

		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
		HttpServletResponse response = (HttpServletResponse) joinPoint.getArgs()[1];

		System.out.println("controller의 메소드가 실행되기 전, HpMemAOP에서 정의한 메소드가 실행됩니다.");
		System.out.println("현재 servletContext : " + request.getServletContext());

		HttpSession session = request.getSession();
		HpMemberVO hpMember = (HpMemberVO) session.getAttribute("hpLoginuser");

		String msg = "로그인이 필요한 페이지입니다!";
		String loc = "";

		if (hpMember == null) {

			loc = request.getContextPath() + "/login.sd";
			request.setAttribute("msg", msg);
			request.setAttribute("loc", loc);

			String url = MyUtil.getCurrentURL(request);

			if (url.endsWith("?null")) {
				url = url.substring(0, url.indexOf("?"));
			}

			System.out.println(url);
			session.setAttribute("gobackURL", url);

			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/msg.jsp");

			try {
				dispatcher.forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Pointcut("execution(public * com.synergy..*Controller.*_cancelPrevSubmission(..) )")
	public void cancelSubmissions() {
	}

	@Before("cancelSubmissions()")
	public void cancelPrevSubmission(JoinPoint joinPoint) {

		HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];

		System.out.println("Before 또 ~");

		HpMemberVO hpMember = (HpMemberVO) request.getSession().getAttribute("hpLoginuser");
		String hpSeq = String.valueOf(hpMember.getHpSeq());

		service.cancelPrevSubmission(hpSeq);
		
	}
	
	
	   
	@Pointcut("execution(public * com.synergy..*Controller.confirmUpdate_*(..) )")
	public void confirmUpdate() {}
		
	@SuppressWarnings("unchecked")  // 앞으로는 노란줄 경고 표시 하지 않겠다는 뜻
	@After("confirmUpdate()")
		public void updateInfo(JoinPoint joinPoint) {
			// JoinPoint joinPoint는 포인트컷 되어진 주업무의 메소드이다.
			
			HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0]; 
			String submitIDs = request.getParameter("infoJoin");
			String[] sArr = submitIDs.split(",");

			int count = 0;
			for(int i=0; i<sArr.length; i++) {
				HpInfoVO hvo = adminService.getHpApplication(sArr[i]);
				
				System.out.println(hvo.getHpName());
				System.out.println(hvo.getHpSeq());
				System.out.println(hvo.getDept());
				int n = adminService.updateHpApplication(hvo);
				count += n;
			}
			
			if(count != sArr.length) {
				System.out.println("에러 ㅎㅎ..;;;");
			} else {
				System.out.println("성공적인듯 ㅎㅎㅎㅎ;;;");
				
			}
		}
	   
	
	@Pointcut("execution(public * com.synergy..*Controller.*_updateSchedule(..) )")
	public void updateSchedule() {
	}
	
	   //_confirmUpdate
		@After("updateSchedule()")
		public void updateHpSchedule(JoinPoint joinPoint) {
			
			HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
			
			String hpSeq = request.getParameter("hpSeq");
			String submitIds = request.getParameter("infoJoin");
				
			String[] submitIdArr = submitIds.split(",");
			
			HashMap<String, String> paraMap = new HashMap<String, String>();
			
			int result = 0;
			for(int i=0; i<submitIdArr.length; i++) {
				
				paraMap.put("submitId", submitIdArr[i]);
				// submitId 통해서 스케줄 가져옴
				List<HashMap<String, String>> scheduleList = adminService.getAllScheduleEdit(paraMap);

				System.out.println(scheduleList.size());
				System.out.println(scheduleList.get(i).get("hpSeq"));
				System.out.println(scheduleList.get(i).get("day"));

				System.out.println(scheduleList.get(i).get("open"));
				System.out.println(scheduleList.get(i).get("close"));

				// 가져온 스케줄을 Schedule에 업데이트
				result += adminService.updateHpSchedule(scheduleList);
			}
			
			if (result == (submitIdArr.length * 6)) {
				System.out.println("성공적인듯 ㅎㅎㅎㅎ;;;");
			} else {
				System.out.println("안 성공적 ㅠ ㅎㅎㅎㅎ;;;");

			}
			
			
		}

}

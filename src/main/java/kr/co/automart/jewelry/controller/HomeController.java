package kr.co.automart.jewelry.controller;

//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.co.automart.jewelry.common.SessionConstant;
import kr.co.automart.jewelry.service.CommonService;



@Controller
//@RestController
@RequestMapping("/")
public class HomeController {

//	@Autowired
//	private ApplicationContext ctx;

	@Autowired
	private CommonService commService;
//	private HomeService homeService;
	
	private	String			_pid = "";
	
	
	@RequestMapping(value="/")
	public String home(HttpServletRequest request, Model model, HttpSession session) {
		
		String view = "errorPage";
		_pid = "home";

		// SESSION ID 추가
		session.setAttribute(SessionConstant.SESSION_ID, request.getSession().getId());
		
		String uuid = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			session.setAttribute(SessionConstant.SESSION_SECRETUUID, uuid);
		}
		model.addAttribute("sid",uuid);
		
		try {
			
			Map<String,String> cm = commService.getMenu(session, model, _pid);
			if (cm != null) {
				view = cm.get("pageView");
			}
			
			boolean isLogined = commService.checkLogin(request,model);
			
			if (cm != null && "false".equals(cm.get("needLogin")) ) {
				commService.initService(session, model, _pid, request);
				model.addAttribute("pid",_pid);
				return view;
				
			}
			
			if (isLogined == false) {
				_pid = "login";
				commService.initService(session, model, _pid, null);
				model.addAttribute("pid",_pid);
				return "errorPage";
			}
			
			commService.initService(session, model, _pid, null);
			
			if (cm != null) {
				view = cm.get("pageView");
			}
			
			
		} catch (Exception e) {
			view = "errorPage";
		}
		
		model.addAttribute("pid",_pid);
		return view;
		
/*		
		
		
		
		
		
//		commService.initService(session, model, _pid, null);
		
		if (commService.checkLogin(request,model) == false) {
			_pid = "login";
			model.addAttribute("pid",_pid);
			return "account/login";
		}
		
//		homeService.getDashBoardInfo(model, session);
		
//		commService.getMenu(session, model, _pid);
		Map<String,String> cm = commService.getMenu(session, model, _pid);
		
		if (cm != null) {
			view = cm.get("pageView");
		}
		
		
		
		
//		List<MemberVo> memberList = memberService.getAllMember();
//		System.out.println("size: " + memberList.size());
		
		
//		model.addAttribute("memberList",memberList);
		model.addAttribute("pid",_pid);
		return view;
		
*/		
		
	}
	
/*	
	
	@RequestMapping(value="/")
	public String home_org(HttpServletRequest request, Model model, HttpSession session) {
		
		String view = "errorPage";
		_pid = "home";
		commService.initService(session, model, _pid, null);
		
		String uuid = UUID.randomUUID().toString();
		model.addAttribute("sid",uuid);
		session.setAttribute(SessionConstant.SESSION_SECRETUUID, uuid);
		
		if (commService.checkLogin(request,model) == false) {
			_pid = "login";
			model.addAttribute("pid",_pid);
			return "account/login";
		}
		
//		homeService.getDashBoardInfo(model, session);
		
//		commService.getMenu(session, model, _pid);
		Map<String,String> cm = commService.getMenu(session, model, _pid);
		
		if (cm != null) {
			view = cm.get("pageView");
		}
		
		
		
		
//		List<MemberVo> memberList = memberService.getAllMember();
//		System.out.println("size: " + memberList.size());
		
		
//		model.addAttribute("memberList",memberList);
		model.addAttribute("pid",_pid);
		return view;
	}
	
*/	
	
	
	
	
	@RequestMapping(value="/{pid}", method = {RequestMethod.GET,RequestMethod.POST})
//	@PostMapping(value="/{pid}" )
	public String pid(@PathVariable String pid, HttpServletRequest request, Model model, HttpSession session) {
		
		if ("favicon.ico".equals(pid)) return "";
		
		String view = "errorPage";
		_pid = pid;
		
		String uuid = (String) session.getAttribute(SessionConstant.SESSION_SECRETUUID);
		
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			session.setAttribute(SessionConstant.SESSION_SECRETUUID, uuid);
		}
		model.addAttribute("sid",uuid);
		
		try {
			
			Map<String,String> cm = commService.getMenu(session, model, _pid);
			if (cm != null) {
				view = cm.get("pageView");
			}
			
			boolean isLogined = commService.checkLogin(request,model);
			
			if (cm != null && "false".equals(cm.get("needLogin")) ) {
				commService.initService(session, model, _pid, request);
				model.addAttribute("pid",_pid);
				return view;
				
			}
			
			if (isLogined == false) {
				//_pid = "login";
				commService.initService(session, model, "login", request);
				model.addAttribute("pid",_pid);
				return "account/login";
			}
			
			commService.initService(session, model, _pid, request);
//			Map<String,String> cm = commService.getMenu(session, model, _pid);
//			if (cm != null) {
//				view = cm.get("pageView");
//			}
			
		} catch (Exception e) {
			view = "errorPage";
		}
		
		model.addAttribute("pid",_pid);
		return view;
	}
	
	
	
/*	
	
	@RequestMapping(value="/{pid}", method = {RequestMethod.GET,RequestMethod.POST})
//	@PostMapping(value="/{pid}" )
	public String pid(@PathVariable String pid, HttpServletRequest request, Model model, HttpSession session) {
		
//		if ("favicon.ico".equals(pid)) return "";
		
		String view = "errorPage";
		_pid = pid;
		
		commService.initService(session, model, _pid, request);
		
		String uuid = UUID.randomUUID().toString();
		model.addAttribute("sid",uuid);
		session.setAttribute(SessionConstant.SESSION_SECRETUUID, uuid);
		
		if (commService.checkLogin(request,model) == false) {
			//_pid = "login";
			model.addAttribute("pid",_pid);
			return "account/login";
		}
		
		Map<String,String> cm = commService.getMenu(session, model, _pid);
		if (cm != null) {
			view = cm.get("pageView");
		}
		
		
//		model.addAttribute("memberList",memberList);
		model.addAttribute("pid",_pid);
		return view;
	}
	
	
*/	
	
/*	
	
	@RequestMapping(value="favicon.ico")
	public ResponseEntity<Resource> favicon(HttpServletRequest request, Model model, HttpSession session) {
		
//		String fileName = "favicon.ico";
		HttpHeaders header = new HttpHeaders();
		Resource favicon = ctx.getResource("static/favicon.ico");
		return new ResponseEntity<Resource>(favicon, header, HttpStatus.OK);
		
/*		
		
		try {
			
			String path =  this.getClass().getResource("/resource/static").getPath();
			FileSystemResource resource = new FileSystemResource(path + fileName);
			if (!resource.exists()) {
				return new ResponseEntity<Resource>(null, header, HttpStatus.OK);
			}
			Path filePath = null;
			filePath = Paths.get(path + fileName);
			header.add("Content-Type", Files.probeContentType(filePath));
			return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
		} catch (Exception e) {
//			throw new NotFoundImageException();
			return new ResponseEntity<Resource>(null, header, HttpStatus.OK);
		}
//		return "/favicon.ico";

	}
	
*/
	
}




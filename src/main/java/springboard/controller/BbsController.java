package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springboard.command.BbsCommandImpl;
import springboard.command.DeleteActionCommand;
import springboard.command.EditActionCommand;
import springboard.command.EditCommand;
import springboard.command.ListCommand;
import springboard.command.ViewCommand;
import springboard.command.WriteActionCommand;
import springboard.model.JDBCTemplateDAO;
import springboard.model.SpringBbsDTO;

@Controller
public class BbsController {
	
	@Autowired
	private JDBCTemplateDAO dao;
	
	//모든 Command객체의 부모타입 참조변수 생성
	BbsCommandImpl command = null;
	
	@Autowired
	ListCommand listCommand;
	@Autowired
	WriteActionCommand writeActionCommand;
	@Autowired
	ViewCommand viewCommand;
	@Autowired
	EditCommand editCommand;
	@Autowired
	DeleteActionCommand deleteActionCommand;
	@Autowired
	EditActionCommand editActionCommand;
	
	
	@RequestMapping("board/list.do")
	public String list(Model model, HttpServletRequest req) {
		
		model.addAttribute("req", req);
		//command = new ListCommand();
		command = listCommand;
		command.execute(model);
		
		return "BootSkin/listT";
	}
	
	@RequestMapping("/board/write.do")
	public String wrtie(Model model) {
		return "BootSkin/writeT";
	}
	
	@RequestMapping(value="/board/writeAction.do", method=RequestMethod.POST)
	public String writeAction(Model model, HttpServletRequest req, SpringBbsDTO springBbsDTO) {
		
		model.addAttribute("req", req);
		model.addAttribute("springBbsDTO", springBbsDTO);
		
		//command = new WriteActionCommand();
		command = writeActionCommand;
		command.execute(model);
		
		return "redirect:list.do?nowPage=1";
	}
	
	@RequestMapping("/board/view.do")
	public String view(Model model, HttpServletRequest req, SpringBbsDTO springBbsDTO) {
		
		model.addAttribute("req", req);
		model.addAttribute("springBbsDTO", springBbsDTO);
		//command = new ViewCommand();
		command = viewCommand;
		command.execute(model);
		
		return "BootSkin/viewT";
	}
	
	@RequestMapping("/board/password.do")
	public String password(Model model, HttpServletRequest req) {
		
		model.addAttribute("idx", req.getParameter("idx"));
		return "BootSkin/passwordT";
	}
	
	@RequestMapping("/board/passwordAction.do")
	public String passwordAction(Model model, HttpServletRequest req) {
		
		String modePage = null;
		
		String mode = req.getParameter("mode");
		String idx = req.getParameter("idx");
		String nowPage = req.getParameter("nowPage");
		String pass = req.getParameter("pass");
		
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		int rowExist = dao.password(idx, pass);
		
		if (rowExist <= 0) {
			model.addAttribute("isCorrMsg", "패스워드가 일치하지 않습니다.");
			model.addAttribute("idx", idx);
			
			modePage = "BootSkin/passwordT";
		}
		else {
			System.out.println("검증완료");
			
			if (mode.equals("edit")) {
				model.addAttribute("req", req);
				//command = new EditCommand();
				command = editCommand;
				command.execute(model);
				
				modePage = "BootSkin/editT";
			}
			else if (mode.equals("delete")) {
				model.addAttribute("req", req);
				//command = new DeleteActionCommand();
				command = deleteActionCommand;
				command.execute(model);
				
				model.addAttribute("nowPage", req.getParameter("nowPage"));
				modePage = "redirect:list.do";
			}
		}
		
		return modePage;
	}
	
	@RequestMapping("/board/editAction.do")
	public String editAction(HttpServletRequest req, 
			Model model, SpringBbsDTO springBbsDTO) {
		
		model.addAttribute("req", req);
		model.addAttribute("springBbsDTO", springBbsDTO);
		//command = new EditActionCommand();
		command = editActionCommand;
		command.execute(model);
		
		model.addAttribute("idx", req.getParameter("idx"));
		model.addAttribute("nowPage", req.getParameter("nowPage"));
		return "redirect:view.do";
	}
}

package com.smartcontactmanager.controller;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helpers.Message;
import com.smartcontactmanager.service.Services;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller

public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Services services;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ContactRepository contactRepository;

	@ResponseBody
	@GetMapping("/home")
	public String test() {
		User user = new User();
		user.setName("Nitesh Kumar");
		user.setEmail("niteshsaini@gmail.com");
		user.setEnabled(true);
		user.setPassword("mypassword");
		user.setRole("USER");
		userRepository.save(user);
		return "User Saved Successfully...";
	}

	@GetMapping("/")
	public String home() {
		return "home";

	}

	@GetMapping("/about")
	public String about() {
		return "about";

	}

	@GetMapping("/login")
	public String signin() {
		return "login";

	}
	@GetMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("User", new User());
		return "signup";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("User") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model m,
			HttpSession session) {
		System.out.println("Agreement " + agreement);
		if (!agreement) {
			System.out.println("You have not agreed the terms and conditions.");
			m.addAttribute("Message", new Message("You have not agreed the terms and conditions.", "alert-danger"));
			m.addAttribute("User", user);
			return "signup";
		}
		if (result.hasErrors()) {
			System.out.println(result.getFieldError("name"));
			System.out.println("error aagayi hai");
			m.addAttribute("Message", new Message("Please enter a valid details.", "alert-danger"));
			m.addAttribute("User", user);
			return "signup";
		}
		user.setRole("ROLE_USER");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEnabled(true);
		services.addUser(user);
		System.out.println("User saved successfullyy");
		m.addAttribute("Message", new Message("New User Registered Successfully.", "alert-success"));
		return "login";

	}

	@PostMapping("/login")
	public String authUser() {
		System.out.println("Login Success url");
		return "dashboard";

	}

	@GetMapping("/dashboard")
	public String dashboard(Principal principal, HttpSession session) {
		System.out.println(principal.getName());
		if (principal.getName() == null) {

			return "login";
		}
		User user = userRepository.findByEmail(principal.getName());
		session.setAttribute("CurrentUser", user);
		return "dashboard";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("CurrentUser");
		return "login";
	}

	@GetMapping("/add-contact")
	public String addContact(HttpSession session, Model model) {
		model.addAttribute("Contact", new Contact());
		model.addAttribute("THeading", "Add New Contact");
		model.addAttribute("Title", "Add Contact");
		model.addAttribute("form-handler", "/add-contact");

		return "addcontact";

	}

	// @ResponseBody
	@PostMapping("/add-contact")
	public String addContactData(@Valid @ModelAttribute("Contact") Contact contact, BindingResult result, Model m,
			Principal principal, @RequestParam("profilePic") MultipartFile file, HttpSession session) {
		if (result.hasErrors()) {
			System.out.println("Some error caused");
			m.addAttribute("Contact", contact);
			m.addAttribute("THeading", "Add New Contact");
			m.addAttribute("Title", "Add Contact");
			m.addAttribute("form-handler", "/add-contact");
			return "addcontact";
		}
		try {
			// System.out.println(path);
			String path = new ClassPathResource("static/images/").getFile().getAbsolutePath();
			InputStream is = file.getInputStream();
			byte[] data = new byte[is.available()];
			is.read(data);
			// System.out.println(Paths.get(path+File.separator+file.getOriginalFilename()));

			FileOutputStream io = new FileOutputStream(path + "/" + file.getOriginalFilename());
			io.write(data);
			io.close();
			System.out.println("File Uploaded Successfully...");
			contact.setImage(file.getOriginalFilename());
			String username = principal.getName();
			User user = userRepository.findByEmail(username);
			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);

			System.out.println("New Contact added Successfully...");
			session.setAttribute("Message",
					new Message("Your Contact has been added successfully...", "alert-success"));

			System.out.println(contact);
			m.addAttribute("Contact", new Contact());
			return "addcontact";

		}
		catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("Message", new Message("Something Went Wrong. Please try again !!", "alert-danger"));
			m.addAttribute("Contact", contact);
			return "addcontact";
		}
	}
	@GetMapping("/view-contacts/{page}")
	public String viewContact(Principal principal, Model m, @PathVariable("page") Integer page, HttpSession session) {
		session.removeAttribute("Message");

		String username = principal.getName();
		User user = userRepository.findByEmail(username);
		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = contactRepository.findAllByUser(user, pageable);

		// List<Contact> contacts=contactRepository.findAllByUser(user,pageable);
		m.addAttribute("Contacts", contacts);
		m.addAttribute("CurrentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "viewcontact";
	}
	@GetMapping("/contact/id={page}")
	public String individualContact(@PathVariable("page") Integer page, Principal principal, Model m,
			HttpSession session) {
		try {
			String username = principal.getName();
			User user = this.userRepository.findByEmail(username);
			List<Contact> contacts = user.getContacts();
			List<Contact> contact = contacts.stream().filter(i -> i.getCid() == page).collect(Collectors.toList());
			// System.out.println(contact);

			// IMPORTANT SECURITY CHECK
			if (user.getId() == contact.get(0).getUser().getId()) {
				m.addAttribute("ActiveContact", contact);
				m.addAttribute("Contact", contact);
				System.out.println("I am here inside contact id page");
				return "Contact";
			}
		} catch (Exception e) {
			System.out.println("Error coming");
		}
		return "errorpage";
	}
	@GetMapping("/contact/delete={id}")
	public String deleteContact(@PathVariable("id") Integer id, Model m, Principal p) {
		String username = p.getName();
		User ActiveUser = userRepository.findByEmail(username);
		Contact con = contactRepository.findById(id).get();
		if (ActiveUser.getId() == con.getUser().getId()) {

			// Allow to delete
			contactRepository.delete(con);
			return "redirect:/view-contacts/0";
		}

		return "errorpage";

	}

	@GetMapping("/contact/update={id}")
	public String updateContact(Model model, @PathVariable("id") Integer id,HttpSession session) {
		User CurrentUser=(User)session.getAttribute("CurrentUser");
		Contact contactValid=contactRepository.findById(id).stream().filter(i->i.getCid()==id).collect(Collectors.toList()).get(0);
		if(CurrentUser.getId()!=contactValid.getUser().getId()){
			System.out.println("Not a Valid User..!!!!");
			session.setAttribute("Message", new Message("You are not authorized to update this contact.", "alert-danger"));
			return "redirect:/view-contacts/0";
			
		}
		Contact contact = contactRepository.findById(id).get();
		model.addAttribute("Contact", contact);
		model.addAttribute("THeading", "Update your Contact");
		model.addAttribute("Title", "Update Contact");
		model.addAttribute("FormHandler", "/contact/update="+id);
		return "addcontact";
	}
	@ResponseBody
	@PostMapping("/contact/update={id}")
	public String updateContactHandler(Model model, @PathVariable("id") Integer id,@Valid @ModelAttribute("Contact") Contact contact, BindingResult result,
	Principal principal, @RequestParam("profilePic") MultipartFile file, HttpSession session) {
		if (result.hasErrors()) {
			System.out.println("Some error caused");
			model.addAttribute("Contact", contact);
			model.addAttribute("THeading", "Update your Contact");
			model.addAttribute("Title", "Update Contact");
			model.addAttribute("FormHandler", "/contact/update="+id);
			return "addcontact";
		}
		try{
			
			String path = new ClassPathResource("static/images/").getFile().getAbsolutePath();
			InputStream is = file.getInputStream();
			byte[] data = new byte[is.available()];
			is.read(data);
			// System.out.println(Paths.get(path+File.separator+file.getOriginalFilename()));

			FileOutputStream io = new FileOutputStream(path + "/" + file.getOriginalFilename());
			io.write(data);
			io.close();
			System.out.println("File Uploaded Successfully...");
			Contact currentContact=contactRepository.findById(id).get();
			currentContact.setDescription(contact.getDescription());
			currentContact.setEmail(contact.getEmail());

			if(!file.isEmpty()){
				File file1=new ClassPathResource("static/images/").getFile();
				File file2=new File(file1,currentContact.getImage());
				System.out.println("Old file name "+currentContact.getImage());
				if(file2.delete()){
					System.out.println("File has been deleted successfully...");
					session.removeAttribute("Message");
				
					System.out.println(session.getAttribute("Message"));
				}
				else{
					System.out.println("Something went wrong.File not deleted");
				}
				System.out.println("New file name "+currentContact.getImage());
			}
			currentContact.setImage(contact.getImage());
			currentContact.setName(contact.getName());
			currentContact.setPhone(contact.getPhone());
			currentContact.setSecondName(contact.getSecondName());
			currentContact.setWork(contact.getWork());
			currentContact.setImage(file.getOriginalFilename());
			contactRepository.save(currentContact);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		model.addAttribute("form-url", "/contact/update="+id);

		return "redirect:/view-contacts/0";
	}

@GetMapping("/profile")
public String profile(Model model){
	model.addAttribute("Title", "Profile");
	return "profile";
	
}

@ResponseBody
@GetMapping("/search-contact/{query}")
public ResponseEntity<?>searchBar(@PathVariable("query") String query,Principal principal){
	String username=principal.getName();
	System.out.println(query);
	User user=userRepository.findByEmail(username);
	System.out.println(username);
	// List<Contact>contacts=contactRepository.findByNameContaining(query);
	// System.out.println(contacts);
	// System.out.println(user);
	return ResponseEntity.ok().body(user);
	
}













}
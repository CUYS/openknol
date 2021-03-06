package ar.com.cuys.webapp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.com.cuys.webapp.entity.Item;
import ar.com.cuys.webapp.entity.Post;
import ar.com.cuys.webapp.entity.Role;
import ar.com.cuys.webapp.entity.User;
import ar.com.cuys.webapp.repository.ItemRepository;
import ar.com.cuys.webapp.repository.PostRepository;
import ar.com.cuys.webapp.repository.RoleRepository;
import ar.com.cuys.webapp.repository.UserRepository;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User findOne(int id) {
		return userRepository.findOne(id);
	}

	@Transactional
	public User findOneWithPosts(int id) {
		User user = findOne(id);
		List<Post> posts = postRepository.findByUser(user);
		for (Post post : posts) {
			List<Item> items = itemRepository.findByPost(post, new PageRequest(0, 10, Direction.DESC, "publishedDate"));
			post.setItems(items);
		}
		user.setPosts(posts);
		return user;
	}

	public void save(User user) {
		user.setEnabled(true);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));			
		
		List<Role> roles = new ArrayList<Role>();		
		roles.add(roleRepository.findByName("ROLE_USER"));
		user.setRoles(roles);
		
		userRepository.save(user);
		
	}

	public User findOneWithPosts(String name) {
		User user = userRepository.findByName(name);
		return findOneWithPosts(user.getId());
	}

	public void delete(int id) {
		userRepository.delete(id);
		
	}

	public User findOne(String username) {
		return userRepository.findByName(username);
		
	}

}

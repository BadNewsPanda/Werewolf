package edu.wm.werewolf.DAO;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.User;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
import static org.springframework.data.mongodb.core.query.Query.query;



import edu.wm.werewolf.config.SpringMongoConfig;
import edu.wm.werewolf.domain.Game;
import edu.wm.werewolf.domain.Kill;
import edu.wm.werewolf.domain.Player;
import edu.wm.werewolf.domain.MyUser;
import edu.wm.werewolf.exceptions.DuplicateUserException;

public class MongoUserDAO implements IUserDAO {

//@Autowired private MongoClient mongo;
//@Autowired private MongoTemplate mongoTemplate;
	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	MongoTemplate mongoTemplate = ctx.getBean(MongoTemplate.class);
	@Override
	public void createUser(MyUser user) throws DuplicateUserException {
		if(!mongoTemplate.collectionExists(MyUser.class))
			mongoTemplate.createCollection(MyUser.class);
		user.setId(UUID.randomUUID().toString());
		if(mongoTemplate.findOne(query(where("username").is(user.getUsername())), MyUser.class)!=null)
			throw new DuplicateUserException(user.getUsername());
		mongoTemplate.insert(user);
	}

	@Override
	public MyUser getUserByName(String username) {
		MyUser u = mongoTemplate.findOne(query(where("username").is(username)), MyUser.class);
		return u;
	}
	
	public void setAllUsersPlaying(boolean value) {
		mongoTemplate.updateMulti(query(where("_class").is("edu.wm.werewolf.domain.MyUser")), new Update().set("isPlaying", value), MyUser.class);
	}
	
	@Override
	public List<MyUser> getAllUsers() {
		return mongoTemplate.findAll(MyUser.class);
	}
	
	@Override
	public void createNewGame(int cycleTime) {
		List<MyUser> users = getAllUsers();
		System.out.println(users.size());
		int werewolfCount = users.size()*3/10;
		if(werewolfCount==0)
			werewolfCount=1;
		HashSet<Integer> j = new HashSet<Integer>();
		while(j.size() < werewolfCount)
			j.add((int)(Math.random()*users.size()));
		for(int n = 0; n<users.size(); n++)
		{
			//System.out.println(users.get(n).getAuthorities().toString());
			if(users.get(n).getAuthorities().toString().equals("[ROLE_ADMIN]"))
			{
				System.out.println("FOUND AN ADMIN");
				continue;
			}
			Player p = new Player(users.get(n).getId(), false, 0.0, 0.0, users.get(n).getUsername(), false, 0);
			
			if(j.contains(n))
				p.setWerewolf(true);
			mongoTemplate.insert(p);
		}
		setAllUsersPlaying(true);
		System.out.println("Finished adding game");
		mongoTemplate.getCollection("players").ensureIndex(new BasicDBObject("location", "2d"));
		Game g = new Game(new Date(), cycleTime);
		mongoTemplate.dropCollection(Game.class);
		mongoTemplate.insert(g);
		
		mongoTemplate.dropCollection(Kill.class);
	}

	@Override
	public boolean removeUserById(String username) {
		MyUser u = mongoTemplate.findOne(query(where("username").is(username)), MyUser.class);
		mongoTemplate.remove(u);
		if(u!=null)
		{
			return true;
		}
		else
			return false;
	}


	
	
}

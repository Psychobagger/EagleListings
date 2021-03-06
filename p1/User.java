/**
 * 
 * @author Tyler Procko
 * @date 10/2017 - 11/2017
 *  
 * User class. Defines functionality for logging in and out, creating a new account,
 * posting items and searching items.
 * 
 */


package p1;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import javafx.beans.property.SimpleBooleanProperty;


public class User
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	/** Is the user logged in or not? Allows GUI to be updated as needed with listeners. */
	private SimpleBooleanProperty isLoggedIn = new SimpleBooleanProperty();

	/** UserID stored when a user is logged in. */
	private int loggedInID;

	/** Name stored when a user is logged in. */
	private String loggedInName;
	
	/** Email stored when a user is logged in. */
	private String loggedInEmail;
	
	/** Password stored when a user is logged in. */
	private String loggedInPassword;
	
	/** Picture URI stored when a user is logged in. */
	private String pictureURI;

	/** Number of items sold stored when a user is logged in. */
	private int itemsSold;
	
	/** Timestamp stored when a user is logged in. */
	private String regDate;
	
	/** Variable storing the user's rank. */
	private boolean isAdmin;
	
	/** ResultSet for querying info from the database. */
	private ResultSet set;
	
	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */

	/**
	 * Default constructor for User.
	 */
	public User()
	{
		// The user created is empty (created when the application is started)
		// So initially, no one is logged in
		isLoggedIn.set(false);
	}

	
	/**
	 * Takes in an input email and password and attempts to match them to a row in the SQL database,
	 * through the class SQLManager.
	 * 
	 * @param email
	 * @param password
	 */
	public void login(String email, String password)
	{
		// Set everything to null values before attempting login
		logout();
		
		// Such try
		try 
		{
			set = MainPage.sqlm.getStatement().executeQuery("SELECT id, name, email, password, admin, itemssold FROM users");
		
			while(set.next())
			{
				// If the input email AND password match ANY email/password combo from the DB exactly, user is logged in
				if ((email.equals(set.getString("email"))) && (password.equals(set.getString("password"))))
				{
					// Store the user's id, name, email and password
					loggedInID = set.getInt("id");					
					loggedInName = set.getString("name");
					loggedInEmail = set.getString("email");
					loggedInPassword = set.getString("password");
					itemsSold = set.getInt("itemssold");
				
					// Set the logged in value so the application's NavBar can update appropriately
					isLoggedIn.set(true);
					
					// Confirmatory login message
					System.out.println("Login successful, " + loggedInName + "!");
					
					// Admin or not
					if (set.getString("admin").equals("true"))
					{
						isAdmin = true;
					}
						
					else
					{
						isAdmin = false;
					}
				}
			}
		}
		
		// Very catch
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.err.println("Failed to find passwords from database!");
		}
	}
	
	
	/**
	 * Method to log the user out.
	 * Simply sets all Java-stored user-info to null or default values.<br>
	 * Does not touch the database in any way.
	 */
	public void logout()
	{
		loggedInName = null;
	    loggedInEmail = null;
	    loggedInPassword = null;
	    pictureURI = null;
		itemsSold = 0;
		regDate = null; 
		
		isLoggedIn.set(false);
	}
	
	
	/**
	 * Provides functionality for creating a new user and adding it to the connected database.<br>
	 * There is no need to include a timestamp on the Java end, as the PHPMyAdmin DB service inputs
	 * it automatically for every new row added.
	 * 
	 * @param name
	 * @param email
	 * @param password
	 * @param pictureURI
	 */
	public void createNewUser(String name, String email, String password)
	{
		// Set everything to null values before attempting login
		logout();
		
		// Set new id to use initially to 0
		int maxid = 0;
		
		// Such try
		try 
		{
			// Set a new id for the user by iterating through all of them
			// Basically finding the biggest id number in the table and adding one to it
			// in order to form the new user's id
			// This is important as it allows items to be added even if the table ids are not sorted
			set = MainPage.sqlm.getStatement().executeQuery("SELECT id FROM users");
			while(set.next())
			{
				// Set current id to the one being iterated over
				int curid = set.getInt("id");
				
				// If current is greater than max, set new max
				if (curid > maxid)
				{
					maxid = curid;
				}
			}
			// New listing id by adding one to max
			maxid++;
			
			// TODO update GUI
			// Iterate over every email in the database and check if the user-input email is already in use
			set = MainPage.sqlm.getStatement().executeQuery("SELECT email FROM users");
			while(set.next())
			{
				// If the input email is already in use, handle it
				if (email.equals(set.getString("email")))
				{
					System.out.println("Email is already in use!");
					return;
				}
			}
			
			// TODO 
			// image selection (right now it's default)
			pictureURI = "../img/avatars/default.png";
			
			// If all is well, create a new user
			// Format is a little strange for entering non-concrete values (i.e. variables) into the database, but it's not terrible
			// For String variables: '"+string+"'
			// For int variables: "+int+"
			// For bools: 'true' or 'false'
			// For null: null
			MainPage.sqlm.getStatement().executeUpdate("INSERT INTO users (id, name, email, password, picture, ratinggood, ratingbad, itemssold, verified, admin) "
	        + "VALUES ("+maxid+", '"+name+"', '"+email+"', '"+password+"', '"+pictureURI+"', 0, 0, 0, 'false', 'false')");
		} 
		
		// Very catch
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.err.println("Could not create new user!");
		}
	}
	
	
	//TODO
	/**
	 * Allows a user to create a new item listing.<br>
	 */
	//test
	//public void createListing()
	public void createListing(String name, String itemCategory, String description, String productCondition, String price, String vehicleYear, String vehicleMiles, String vehicleBrand, String vehicleType,
			String coursePrefix, String furnitureCategory, String furnitureRoomCategory, Integer roomBedNum, Integer roomBathNum, String roomAddress)
	{	
		// Set a new id to 0
		int maxid = 0;
		
		// Such try
		try 
		{
			// Set a new id for the user by iterating through all of them
			// Basically finding the biggest id number in the table and adding one to it
			// in order to form the new listing id
			// This is important as it allows items to be added even if the table ids are not sorted
			set = MainPage.sqlm.getStatement().executeQuery("SELECT id FROM listings");
			while(set.next())
			{
				// Set current id to the one being iterated over
				int curid = set.getInt("id");
				
				// If current is greater than max, set new max
				if (curid > maxid)
				{
					maxid = curid;
				}
			}
			// New listing id by adding one to max
			maxid++;
			
			// These next four if statements would not be necessary, but inputting a string value that may be null
			// results in the string "null" being input into the DB... this I cannot allow
			if (Objects.equals(itemCategory, "books"))
			{
				MainPage.sqlm.getStatement().executeUpdate("INSERT INTO listings (id, sellerid, name, category, description, listingcondition, price, vehicleyear, vehiclemiles, vehicletype, vehiclebrand, bookprefix, furncategory, furnroomcategory, roombednum, roombathnum, roomaddress) "
						+ "VALUES ("+maxid+", "+loggedInID+", '"+name+"', '"+itemCategory+"', '"+description+"', '"+productCondition+"', '"+price+"', null, null, null, null, '"+coursePrefix+"', null, "
						+ "null, null, null, null)");
			}
			
			if (Objects.equals(itemCategory, "vehicles"))
			{
				MainPage.sqlm.getStatement().executeUpdate("INSERT INTO listings (id, sellerid, name, category, description, listingcondition, price, vehicleyear, vehiclemiles, vehicletype, vehiclebrand, bookprefix, furncategory, furnroomcategory, roombednum, roombathnum, roomaddress) "
						+ "VALUES ("+maxid+", "+loggedInID+", '"+name+"', '"+itemCategory+"', '"+description+"', '"+productCondition+"', '"+price+"', '"+vehicleYear+"', '"+vehicleMiles+"', '"+vehicleType+"', '"+vehicleBrand+"', null, null, "
						+ "null, null, null, null)");
			}
			
			if (Objects.equals(itemCategory, "furniture"))
			{
				MainPage.sqlm.getStatement().executeUpdate("INSERT INTO listings (id, sellerid, name, category, description, listingcondition, price, vehicleyear, vehiclemiles, vehicletype, vehiclebrand, bookprefix, furncategory, furnroomcategory, roombednum, roombathnum, roomaddress) "
						+ "VALUES ("+maxid+", "+loggedInID+", '"+name+"', '"+itemCategory+"', '"+description+"', '"+productCondition+"', '"+price+"', null, null, null, null, null, '"+furnitureCategory+"', "
						+ "'"+furnitureRoomCategory+"', null, null, null)");
			}
			
			if (Objects.equals(itemCategory, "rooms"))
			{
				MainPage.sqlm.getStatement().executeUpdate("INSERT INTO listings (id, sellerid, name, category, description, listingcondition, price, vehicleyear, vehiclemiles, vehicletype, vehiclebrand, bookprefix, furncategory, furnroomcategory, roombednum, roombathnum, roomaddress) "
						+ "VALUES ("+maxid+", "+loggedInID+", '"+name+"', '"+itemCategory+"', '"+description+"', '"+productCondition+"', '"+price+"', null, null, null, null, null, null, "
						+ "null, "+roomBedNum+", "+roomBathNum+", '"+roomAddress+"')");
			}
			
			// Increment items sold in the database
			int newItemsSold = itemsSold + 1;
			MainPage.sqlm.getStatement().executeUpdate("UPDATE users SET itemssold = ("+newItemsSold+") WHERE id = ("+loggedInID+")");
			
			System.out.println("Item listed successfully!");
		} 
		
		// Very catch
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.err.println("Could not create a new listing in the database!");
		}
	}
	
	
	/**
	 * Functionality for user searching through the search bar in the NavBar class.
	 * Uses SQL commands to return any "like" results, based on user input.
	 * 
	 * @param userSearch
	 * @return searchResults
	 */
	public ArrayList<Listing> search(String userSearch)
	{
		ArrayList<Listing> searchResults = new ArrayList<>();
		try 
		{
			// Execute a query, grabbing all listings where the title is similar to the user's entered search term
			set = MainPage.sqlm.getStatement().executeQuery("SELECT * FROM listings WHERE name LIKE '%"+userSearch+"%'");
			//ResultSet set2 = MainPage.sqlm.getStatement().executeQuery("SELECT email FROM users WHERE id = ("+set.getString("sellerid")+")");
			
			// TODO
			//while (set.next() && set2.next())
			while (set.next())
			{
				// TODO
				//String email = set2.getString("email");
				//int sellerid = set.getInt("sellerid");
				
				String specificInfo = "";
				
				if (set.getString("category").equals("books"))
				{
					specificInfo = "Course prefix: " + set.getString("bookprefix");
				}
				
				if (set.getString("category").equals("vehicles"))
				{
					specificInfo = "Year: " + set.getString("vehicleyear") + "\n" + "Miles: " + set.getString("vehiclemiles")
						+ "\n" + "Brand: " + set.getString("vehiclebrand") + "\n" + "Type: " + set.getString("vehicletype");
				}
				
				if (set.getString("category").equals("furniture"))
				{
					specificInfo = "Type: " + set.getString("furncategory") + "\n" + "Room type: " + set.getString("furnroomcategory");
				}
				
				if (set.getString("category").equals("rooms"))
				{
					specificInfo = "Number of bedrooms: " + set.getString("roombednum") + "\n" + "Number of bathrooms: " + set.getString("roombathnum") 
						+ "\n" + "Room address: " + set.getString("roomaddress");
				}
				
				searchResults.add(new Listing(set.getString("name"), set.getString("description"), set.getString("category"), set.getString("listingcondition"), set.getString("price"), specificInfo));
			}
		} 
		
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		return searchResults;
	}
	
	
	/**
	 * Finds all posted items given an item type.
	 * 
	 * @param itemType
	 * @return itemResults
	 */
	public ArrayList<Listing> findItems(String itemType)
	{
		ArrayList<Listing> itemResults = new ArrayList<>();
		
		try 
		{
			set = MainPage.sqlm.getStatement().executeQuery("SELECT * FROM listings WHERE category = '"+itemType+"'");
			
			while(set.next())
			{
				String specificInfo = "";
				
				if (set.getString("category").equals("books"))
				{
					specificInfo = "Course prefix: " + set.getString("bookprefix");
				}
				
				if (set.getString("category").equals("vehicles"))
				{
					specificInfo = "Year: " + set.getString("vehicleyear") + "\n" + "Miles: " + set.getString("vehiclemiles")
						+ "\n" + "Brand: " + set.getString("vehiclebrand") + "\n" + "Type: " + set.getString("vehicletype");
				}
				
				if (set.getString("category").equals("furniture"))
				{
					specificInfo = "Type: " + set.getString("furncategory") + "\n" + "Room type: " + set.getString("furnroomcategory");
				}
				
				if (set.getString("category").equals("rooms"))
				{
					specificInfo = "Bedroom number: " + set.getString("roombednum") + "\n" + "Bathroom number: " + set.getString("roombathnum") 
						+ "\n" + "Room address: " + set.getString("roomaddress");
				}
				
				itemResults.add(new Listing(set.getString("name"), set.getString("description"), set.getString("category"), set.getString("listingcondition"), set.getString("price"), specificInfo));
			}
		} 
		
		catch (SQLException e) 
		{
			System.err.println("Could not retrieve listed items given type.");
			e.printStackTrace();
		}
		
		return itemResults;
	}
	
	
	/**
	 * Getter for primitive logged in value.
	 * 
	 * @return isLoggedIn
	 */
	public boolean isLoggedIn() 
	{
		return isLoggedIn.get();
	}
	
	
	/**
	 * Setter
	 * 
	 * @param isLoggedIn
	 */
	public void setLoggedIn(boolean isLoggedIn)
	{
		this.isLoggedIn.set(isLoggedIn);
	}
	
	
	/**
	 * Getter for logged in wrapper
	 * 
	 * @return isLoggedIn
	 */
	public SimpleBooleanProperty getIsLoggedInProperty()
	{
		return isLoggedIn;
	}
	
	
	/**
	 * Getter
	 * 
	 * @return isAdmin
	 */
	public boolean isAdmin()
	{
		return isAdmin;
	}
	
	
	/**
	 * Getter
	 * 
	 * @return loggedInID
	 */
	public int getLoggedInID() 
	{
		return loggedInID;
	}
	
	
	/** 
	 * Getter- actually retrieves the user's email from the DB when called,
	 * unlike other getters. Needed so the user can update email.
	 * 
	 * @return loggedInEmail
	 */
	public String getLoggedInEmail() 
	{
		// Such try
		try 
		{
			set = MainPage.sqlm.getStatement().executeQuery("SELECT email FROM users WHERE id = ("+loggedInID+")");
			
			// Must check set.next() otherwise we get 'SQLexception before start of result set' error
			// https://stackoverflow.com/questions/8826247/java-sql-sqlexception-before-start-of-result-set
			if (set.next())
			{
				loggedInEmail = set.getString("email");
			}
		} 
		
		// Very catch
		catch (SQLException e) 
		{
			System.err.println("Could not retrieve email!");
			e.printStackTrace();
		}
		
		return loggedInEmail;
	}


	/**
	 * Getter
	 * 
	 * @return loggedInName
	 */
	public String getName() 
	{
		return loggedInName;
	}


	/**
	 * Getter- actually retrieves the user's password from the DB when called,
	 * unlike other getters. Needed so the user can update password.
	 * 
	 * @return loggedInPassword
	 */
	public String getLoggedInPassword() 
	{
		// Such try
		try 
		{
			set = MainPage.sqlm.getStatement().executeQuery("SELECT password FROM users WHERE id = ("+loggedInID+")");
			
			// Must check set.next() otherwise we get 'SQLexception before start of result set' error
			// https://stackoverflow.com/questions/8826247/java-sql-sqlexception-before-start-of-result-set
			if (set.next())
			{
				loggedInEmail = set.getString("password");
			}
		} 
		
		// Very catch
		catch (SQLException e) 
		{
			System.err.println("Could not retrieve password!");
			e.printStackTrace();
		}
		
		return loggedInPassword;
	}
	
	
	/**
	 * Getter for the user's profile picture URI <br>
	 * In the URL/URI world, forward and backslash count as the same thing, apparently
	 * 
	 * @return pictureURI
	 */
	public String getPictureURI() 
	{
		return pictureURI;
	}


	/**
	 * Getter
	 * 
	 * @return itemsSold
	 */
	public int getItemsSold()
	{
		return itemsSold;
	}


	/**
	 * Getter
	 * 
	 * @return regDate
	 */
	public String getRegDate() 
	{
		return regDate;
	}
}

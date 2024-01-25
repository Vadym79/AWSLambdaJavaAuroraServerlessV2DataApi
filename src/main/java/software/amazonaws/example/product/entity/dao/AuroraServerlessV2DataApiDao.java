package software.amazonaws.example.product.entity.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.BeginTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.BeginTransactionResponse;
import software.amazon.awssdk.services.rdsdata.model.CommitTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.CommitTransactionResponse;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.RollbackTransactionRequest;
import software.amazon.awssdk.services.rdsdata.model.RollbackTransactionResponse;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;
import software.amazonaws.example.product.entity.Product;
import software.amazonaws.example.product.entity.User;
import software.amazonaws.example.product.entity.UserAddress;

public class AuroraServerlessV2DataApiDao {
	
	private static final RdsDataClient rdsDataClient = RdsDataClient.builder().build();
	
	private final String dbEndpoint = System.getenv("DB_ENDPOINT");
	private final String dbName = System.getenv("DB_NAME");
	private final String dbClusterArn = System.getenv("DB_CLUSTER_ARN");
	private final String dbSecretStoreArn = System.getenv("DB_CRED_SECRETS_STORE_ARN");
	
	private static final String CREATE_USER_SEQ= "CREATE SEQUENCE user_id START 1;"; 
	private static final String CREATE_USER_ADDRESS_SEQ= "CREATE SEQUENCE user_address_id START 1;"; 
	
	private static final String CREATE_USER_TABLE = "CREATE TABLE tbl_user ( \n"+
		    "id bigint NOT NULL, \n"+
		    "first_name varchar(255) NOT NULL, \n"+
		    "last_name varchar(255) NOT NULL, \n"+
		    "email varchar(128) NOT NULL, \n"+
		    "PRIMARY KEY (id)   \n"+ 
		");";
	
	private static final String CREATE_USER_ADDRESS_TABLE = "CREATE TABLE tbl_user_address ( \n"+
		    "id bigint NOT NULL,\n"+
		    "user_id bigint NOT NULL, \n"+
		    "street varchar(255) NOT NULL,\n"+
		    "city varchar(255) NOT NULL, \n"+
		    "country varchar(128) NOT NULL, \n"+
		    "zip varchar(64) NOT NULL, \n"+
		    "PRIMARY KEY (id),  \n"+
		    "CONSTRAINT fk_user_id  \n"+
		     " FOREIGN KEY(user_id)  \n"+
			    "REFERENCES tbl_user(id) \n"+
		");";

	public Optional<Product> getProductById(final String id) {
				
		final String sql="select id, name, price from products where id=:id";
		final SqlParameter sqlParam= SqlParameter.builder().name("id").value(Field.builder().longValue(Long.valueOf(id)).build()).build();
		System.out.println(" sql param "+sqlParam);
		final ExecuteStatementRequest request= ExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(sql).
				parameters(sqlParam).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse response= rdsDataClient.executeStatement(request);
		final List<List<Field>> records=response.records();
		
		if (records.isEmpty()) { 
			System.out.println("no records found");
			return Optional.empty();
		}
		
		System.out.println("response records: "+records);
		
		final List<Field> fields= records.get(0);
		final String name= fields.get(1).stringValue(); 
		final BigDecimal price= new BigDecimal(fields.get(2).stringValue());
		final Product product = new Product(id, name, price);
		System.out.println("Product :"+product);
		
		return Optional.of(product);
	}

	
	public void createTableAndSequences () {
		this.createTableAndSequences(CREATE_USER_SEQ);
		this.createTableAndSequences(CREATE_USER_ADDRESS_SEQ);
		this.createTableAndSequences(CREATE_USER_TABLE);
		this.createTableAndSequences(CREATE_USER_ADDRESS_TABLE);
	}
	
    private void createTableAndSequences(String sql) {

		System.out.println("dbEndpoint: "+dbEndpoint+ " dbName: "+dbName+ " dbclusterARN: "+dbClusterArn+ " dbSecretStoreARN: "+dbSecretStoreArn);
		System.out.println("execute sql "+sql);
		final ExecuteStatementRequest request= ExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(sql).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse response= rdsDataClient.executeStatement(request);
		final List<List<Field>> records=response.records();
		
		System.out.println("records "+records);
	}

	private long getNextSequenceValue(final String sequenceName) {
		
		final String sql="SELECT nextval('"+sequenceName+"');";

		System.out.println(" get next value for sequence: "+sql);
		System.out.println("dbEndpoint: "+dbEndpoint+ " dbName: "+dbName+ " dbclusterARN: "+dbClusterArn+ " dbSecretStoreARN: "+dbSecretStoreArn);
		final ExecuteStatementRequest request= ExecuteStatementRequest.builder().database("").
				resourceArn(dbClusterArn).
				secretArn(dbSecretStoreArn).
				sql(sql).
				//formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse response= rdsDataClient.executeStatement(request);
		final List<List<Field>> records=response.records();
		
		if (records.isEmpty()) { 
			System.out.println("no next sequence value found for sequence "+sequenceName);
			throw new RuntimeException("no next sequence value found for sequence "+sequenceName);
		}	
		System.out.println("response records: "+records);
	
		final List<Field> fields= records.get(0);
		final long sequenceNextValue= fields.get(0).longValue(); 
		System.out.println("next sequence value found for sequence "+sequenceName+ " "+sequenceNextValue);
		
		return sequenceNextValue;
	}
	
	private void createUser(final User user, final String transactionId) {

		long userId= getNextSequenceValue("user_id");
		user.setId(userId);
		
		final String createUserSql = "INSERT INTO tbl_user (id, first_name, last_name, email) \n"
				+ "VALUES (:id, :firstName, :lastName, :email);";

		System.out.println("creating user "+createUserSql);
		final SqlParameter userIdParam = SqlParameter.builder().name("id")
				.value(Field.builder().longValue(user.getId()).build()).build();
		final SqlParameter firstNameParam = SqlParameter.builder().name("firstName")
				.value(Field.builder().stringValue(user.getFirstName()).build()).build();
		final SqlParameter lastNameParam = SqlParameter.builder().name("lastName")
				.value(Field.builder().stringValue(user.getLastName()).build()).build();
		final SqlParameter emailParam = SqlParameter.builder().name("email")
				.value(Field.builder().stringValue(user.getEmail()).build()).build();

		final ExecuteStatementRequest createUserRequest = ExecuteStatementRequest.builder().database("")
				.resourceArn(dbClusterArn).secretArn(dbSecretStoreArn).sql(createUserSql)
				.parameters(userIdParam, firstNameParam, lastNameParam, emailParam).transactionId(transactionId).
				// formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse createUserResponse = rdsDataClient.executeStatement(createUserRequest);
		System.out.println(" create user response records:  " + createUserResponse.records());

	}
	
	
	private void createUserAddress(final User user, final String transactionId) {

		long userAddressId = getNextSequenceValue("user_address_id");
		final UserAddress userAddress = user.getUserAddress();
		userAddress.setId(userAddressId);
		
		final String creaeUserAddressSql = "INSERT INTO tbl_user_address (id, user_id, street, city, country, zip) \n"
				+ "VALUES (:id, :userId, :street, :city, :country, :zip);";

		System.out.println("creating user address "+creaeUserAddressSql);
		final SqlParameter userAddressIdParam = SqlParameter.builder().name("id")
				.value(Field.builder().longValue(userAddress.getId()).build()).build();
		final SqlParameter userIdParam1 = SqlParameter.builder().name("userId")
				.value(Field.builder().longValue(user.getId()).build()).build();
		final SqlParameter streetParam = SqlParameter.builder().name("street")
				.value(Field.builder().stringValue(userAddress.getStreet()).build()).build();
		final SqlParameter cityParam = SqlParameter.builder().name("city")
				.value(Field.builder().stringValue(userAddress.getCity()).build()).build();
		final SqlParameter countryParam = SqlParameter.builder().name("country")
				.value(Field.builder().stringValue(userAddress.getCountry()).build()).build();
		final SqlParameter zipParam = SqlParameter.builder().name("zip")
				.value(Field.builder().stringValue(userAddress.getZip()).build()).build();
	
		
		final ExecuteStatementRequest createUserAddressRequest = ExecuteStatementRequest.builder().database("")
				.resourceArn(dbClusterArn).secretArn(dbSecretStoreArn).sql(creaeUserAddressSql)
				.parameters(userAddressIdParam, userIdParam1, streetParam, cityParam, countryParam, zipParam).transactionId(transactionId).
				// formatRecordsAs(RecordsFormatType.JSON).
				build();
		final ExecuteStatementResponse createUserAddressResponse = rdsDataClient.executeStatement(createUserAddressRequest);
		System.out.println(" create user address response records:  " + createUserAddressResponse.records());
	}
	
	public User createUserAndAddressTransactional(final User user) {

		System.out.println("dbEndpoint: " + dbEndpoint + " dbName: " + dbName + " dbclusterARN: " + dbClusterArn
				+ " dbSecretStoreARN: " + dbSecretStoreArn);
		String transactionId = null;
		try {
			final BeginTransactionRequest transactionBeginRequest = BeginTransactionRequest.builder().database("")
					.resourceArn(dbClusterArn).secretArn(dbSecretStoreArn).build();

			final BeginTransactionResponse transactionBeginResponse = rdsDataClient
					.beginTransaction(transactionBeginRequest);
			transactionId = transactionBeginResponse.transactionId();
			System.out.println("began transaction " + transactionId);

			this.createUser(user, transactionId);
			this.createUserAddress(user, transactionId);

			System.out.println("commit transaction " + transactionId);
			final CommitTransactionRequest transactionCommitRequest = CommitTransactionRequest.builder()
					.resourceArn(dbClusterArn).secretArn(dbSecretStoreArn).transactionId(transactionId).build();

			final CommitTransactionResponse transactionCommitResponse = rdsDataClient
					.commitTransaction(transactionCommitRequest);
			System.out.println("transaction commit status:  " + transactionCommitResponse.transactionStatus());
			System.out.println("created user "+user);
			return user;
		} catch (Exception ex) {
			System.out.println(" exception thrown:  " + ex.getMessage());
			if (transactionId != null) {
				System.out.println(" rollback transaction " + transactionId);
				final RollbackTransactionRequest transactionRollbackRequest = RollbackTransactionRequest.builder()
						.resourceArn(dbClusterArn).secretArn(dbSecretStoreArn).transactionId(transactionId).build();
				final RollbackTransactionResponse transactionRollbackResponse = rdsDataClient
						.rollbackTransaction(transactionRollbackRequest);
				System.out.println("transaction rollback status:  " + transactionRollbackResponse.transactionStatus());
			}
		}
		return null;
	}
}
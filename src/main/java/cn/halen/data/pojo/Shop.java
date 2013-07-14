package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

public class Shop {
	private int id;
	
	private String sellerNick;
	
	private String token;
	
	private String refreshToken;
	
	private Date modified;
	
	private Date created;
	
	private Date lastRefresh;
	
	private int autoSync;
	
	private int autoSyncStore;

    private float rate;

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getAutoSyncStore() {
		return autoSyncStore;
	}

	public void setAutoSyncStore(int autoSyncStore) {
		this.autoSyncStore = autoSyncStore;
	}

	private String type;
	
	private int dId;
	
	private Distributor d;
	
	private List<User> userList;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSellerNick() {
		return sellerNick;
	}

	public void setSellerNick(String sellerNick) {
		this.sellerNick = sellerNick;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getdId() {
		return dId;
	}

	public void setdId(int dId) {
		this.dId = dId;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(Date lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public int getAutoSync() {
		return autoSync;
	}

	public void setAutoSync(int autoSync) {
		this.autoSync = autoSync;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Distributor getD() {
		return d;
	}

	public void setD(Distributor d) {
		this.d = d;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	
}

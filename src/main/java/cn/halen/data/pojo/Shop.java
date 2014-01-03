package cn.halen.data.pojo;

import java.util.Date;
import java.util.List;

public class Shop {
	private int id;
	
	private String seller_nick;

    private String appkey;

    private String appsecret;
	
	private String token;
	
	private String refresh_token;
	
	private Date modified;
	
	private Date created;
	
	private Date last_refresh;
	
	private int auto_sync;
	
	private int auto_sync_store;

    private float rate;

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

	private String type;

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    private int distributor_id;
	
	private Distributor d;
	
	private List<User> userList;

	public int getId() {
		return id;
	}

    public String getSeller_nick() {
        return seller_nick;
    }

    public void setSeller_nick(String seller_nick) {
        this.seller_nick = seller_nick;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Date getLast_refresh() {
        return last_refresh;
    }

    public void setLast_refresh(Date last_refresh) {
        this.last_refresh = last_refresh;
    }

    public int getAuto_sync() {
        return auto_sync;
    }

    public void setAuto_sync(int auto_sync) {
        this.auto_sync = auto_sync;
    }

    public int getAuto_sync_store() {
        return auto_sync_store;
    }

    public void setAuto_sync_store(int auto_sync_store) {
        this.auto_sync_store = auto_sync_store;
    }

    public int getDistributor_id() {
        return distributor_id;
    }

    public void setDistributor_id(int distributor_id) {
        this.distributor_id = distributor_id;
    }

    public void setId(int id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

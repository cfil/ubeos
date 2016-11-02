package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reply_stat")
public class ReplyStat extends MyModel {
	
	@Column( name ="request_id", nullable = false)
	public Long request_id;

	@Column(name = "enable", nullable = false)
	public boolean enable = true;

	@Column(name = "replied", nullable = false)
	public boolean replied = false;
	
	@ManyToOne
	public Provider provider;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public ReplyStat(){
	}

	public ReplyStat(Long request_id, boolean enable, boolean replied,
			Provider provider) {
		super();
		this.request_id = request_id;
		this.enable = enable;
		this.replied = replied;
		this.provider = provider;
	}

	public static List<ReplyStat> findByRequestID(Long request_id){
		return ReplyStat.find("byRequest_id", request_id).fetch();
	}
	
	public static ReplyStat findByRequestIDandProvider(Long request_id, Provider provider){
		return ReplyStat.find("byRequest_idAndProvider", request_id, provider).first();
	}
	
	public static List<ReplyStat> findByProvider(Provider provider){
		return ReplyStat.find("byProvider", provider).fetch();
	}
	
}

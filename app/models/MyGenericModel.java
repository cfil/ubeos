package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Type;

import play.db.jpa.GenericModel;

@MappedSuperclass
public class MyGenericModel extends GenericModel{

	@Column(name = "created_at")
	public Date created_at;

	@Column(name = "updated_at")
	public Date updated_at;

	@PreUpdate
	public void onUpdate() {
		updated_at = new Date();
	}

	@PrePersist
	public void onCreate() {
		created_at = new Date();
		updated_at = created_at;
	}
}

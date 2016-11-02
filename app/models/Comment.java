package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.MaxSize;
import play.data.validation.Required;

@Entity
@Table(name = "comment")
public class Comment extends MyModel {
	
	@Required
	@Column(name = "text")
	@MaxSize(value = 1024)
	public String text;

	@Column(name = "enable", nullable = false)
	public boolean enable = false;

	@ManyToOne
	public TravelRequest request;
	
	@ManyToOne
	public Company company;
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public Comment(){
	}

	public Comment(String text){
		this.text = text;
	}
	
	public static List<Comment> getCommentByCompanyAndRequest(Company company, TravelRequest travelRequest){
		return Comment.find("byCompanyAndRequest", company, travelRequest).fetch();
	}

}

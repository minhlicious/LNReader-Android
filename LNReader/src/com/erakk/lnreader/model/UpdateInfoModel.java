package com.erakk.lnreader.model;

import java.util.Date;

public class UpdateInfoModel {

	private int id = -1;
	private UpdateType updateType;
	private String updateTitle;
	private Date updateDate;

	private String updatePage;
	private PageModel updatePageModel;

	private boolean isSelected;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UpdateType getUpdateType() {
		return updateType;
	}
	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}
	public String getUpdateTitle() {
		return updateTitle;
	}
	public void setUpdateTitle(String updateTitle) {
		this.updateTitle = updateTitle;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdatePage() {
		return updatePage;
	}
	public void setUpdatePage(String updatePage) {
		this.updatePage = updatePage;
	}
	public PageModel getUpdatePageModel() {
		return updatePageModel;
	}
	public void setUpdatePageModel(PageModel updatePageModel) {
		this.updatePageModel = updatePageModel;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}



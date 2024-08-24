package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;

	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscriberDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (this.entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (this.service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			
			this.entity = getFormData();
			this.service.saveOrUpdate(this.entity);
			
			this.notifyDataChangeListener();
			
			Utils.currentStage(event).close();
			
		}catch (ValidationException e) {
			this.setErrorMessages(e.getErrors());
		}		
		catch (DbException e) {
			Alerts.showAlert("Error saving obj", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListener() {
		
		this.dataChangeListeners.forEach(listener -> {
			listener.onDataChange();
		});
		
	}

	private Department getFormData() {

		Department obj = new Department();

		ValidationException excepetion = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("") ){
			excepetion.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());

		if(excepetion.getErrors().size() > 0) {
			throw excepetion;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		this.initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if (this.entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		this.txtId.setText(String.valueOf(this.entity.getId()));
		this.txtName.setText(this.entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			this.labelErrorName.setText(errors.get("name"));
		}
		
	}

}

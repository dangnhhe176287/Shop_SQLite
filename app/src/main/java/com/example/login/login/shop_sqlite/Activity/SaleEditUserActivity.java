package com.example.login.login.shop_sqlite.Activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.login.shop_sqlite.Api.ApiClient;
import com.example.login.login.shop_sqlite.Api.SaleApiService;
import com.example.login.login.shop_sqlite.Dto.SaleUserResponseDto;
import com.example.login.login.shop_sqlite.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaleEditUserActivity extends AppCompatActivity {

    private static final String TAG = "EditUserActivity";
    public static final String EXTRA_USER_ID = "extra_user_id";

    private EditText edtUserId, edtEmail, edtPassword, edtPhone,
            edtUserName, edtDateOfBirth, edtAddress, edtCreateDate;
    private Spinner spinnerIsDeleted;
    private Spinner spinnerStatus;
    private Spinner spinnerRoleId;
    private Button btnSaveUser;

    private SaleApiService saleApiService;
    private int currentUserId;
    private Calendar calendar;

    private SimpleDateFormat displaySdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    private String[] roleValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_activity_edit_user);

        edtUserId = findViewById(R.id.edtUserId);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtUserName = findViewById(R.id.edtUserName);
        edtDateOfBirth = findViewById(R.id.edtDateOfBirth);
        edtAddress = findViewById(R.id.edtAddress);
        edtCreateDate = findViewById(R.id.edtCreateDate);
        spinnerIsDeleted = findViewById(R.id.spinnerIsDeleted);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerRoleId = findViewById(R.id.spinnerRoleId);

        btnSaveUser = findViewById(R.id.btnSaveUser);

        saleApiService = ApiClient.getClient().create(SaleApiService.class);
        calendar = Calendar.getInstance();

        currentUserId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Không tìm thấy ID người dùng để chỉnh sửa.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Không tìm thấy ID người dùng trong Intent.");
            finish();
            return;
        }
        edtUserId.setEnabled(false);
        edtCreateDate.setEnabled(false);

        roleValues = getResources().getStringArray(R.array.role_options_values);

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(this,
                R.array.role_options_display, android.R.layout.simple_spinner_item);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoleId.setAdapter(roleAdapter);

        ArrayAdapter<CharSequence> isDeletedAdapter = ArrayAdapter.createFromResource(this,
                R.array.is_deleted_options, android.R.layout.simple_spinner_item);
        isDeletedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIsDeleted.setAdapter(isDeletedAdapter);

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_options, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        edtDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        btnSaveUser.setOnClickListener(v -> saveUser());

        fetchUserDetails(currentUserId);
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateOfBirthField();
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void updateDateOfBirthField() {
        edtDateOfBirth.setText(displaySdf.format(calendar.getTime()));
    }

    private void fetchUserDetails(int userId) {
        Log.d(TAG, "Đang tải chi tiết người dùng với ID: " + userId);
        saleApiService.getUserById(userId).enqueue(new Callback<SaleUserResponseDto>() {
            @Override
            public void onResponse(Call<SaleUserResponseDto> call, Response<SaleUserResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SaleUserResponseDto user = response.body();
                    Log.d(TAG, "Đã tải thành công chi tiết người dùng: " + user.getUserId());
                    populateUserFields(user);
                } else {
                    String errorMsg = "Lỗi khi tải chi tiết người dùng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String rawErrorBody = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + rawErrorBody;
                            Log.e(TAG, "Lỗi phản hồi API (" + response.code() + ") khi tải: " + rawErrorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody khi tải: " + e.getMessage(), e);
                    }
                    Toast.makeText(SaleEditUserActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SaleUserResponseDto> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi tải:", t);
                Toast.makeText(SaleEditUserActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void populateUserFields(SaleUserResponseDto user) {
        edtUserId.setText(user.getUserId() != null ? String.valueOf(user.getUserId()) : "");

        if (user.getRoleId() != null) {
            String roleIdString = String.valueOf(user.getRoleId());
            for (int i = 0; i < roleValues.length; i++) {
                if (roleValues[i].equals(roleIdString)) {
                    spinnerRoleId.setSelection(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < roleValues.length; i++) {
                if (roleValues[i].equals("2")) {
                    spinnerRoleId.setSelection(i);
                    break;
                }
            }
        }

        edtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        edtPassword.setText(user.getPassword() != null ? user.getPassword() : "");
        edtPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        edtUserName.setText(user.getUserName() != null ? user.getUserName() : "");

        if (user.getDateOfBirth() != null) {
            edtDateOfBirth.setText(displaySdf.format(user.getDateOfBirth()));
            calendar.setTime(user.getDateOfBirth());
        } else {
            edtDateOfBirth.setText("");
        }

        edtAddress.setText(user.getAddress() != null ? user.getAddress() : "");

        if (user.getCreateDate() != null) {
            edtCreateDate.setText(apiSdf.format(user.getCreateDate()));
        } else {
            edtCreateDate.setText("");
        }

        if (user.getStatus() != null) {
            if (user.getStatus() == 1) {
                spinnerStatus.setSelection(1);
            } else {
                spinnerStatus.setSelection(0);
            }
        } else {
            spinnerStatus.setSelection(0);
        }

        // Đặt lựa chọn cho Spinner IsDeleted
        if (user.isIsDelete()) {
            spinnerIsDeleted.setSelection(1);
        } else {
            spinnerIsDeleted.setSelection(0);
        }
    }

    private void saveUser() {
        String userIdStr = edtUserId.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String userName = edtUserName.getText().toString().trim();
        String dateOfBirthStr = edtDateOfBirth.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String createDateStr = edtCreateDate.getText().toString().trim();

        Integer roleId = Integer.parseInt(roleValues[spinnerRoleId.getSelectedItemPosition()]);

        Integer status = spinnerStatus.getSelectedItemPosition();

        boolean isDelete = spinnerIsDeleted.getSelectedItemPosition() == 1;

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Email và Mật khẩu.", Toast.LENGTH_LONG).show();
            return;
        }

        Integer userId = null;
        Date dateOfBirth = null, createDate = null;

        try {
            if (!userIdStr.isEmpty()) {
                userId = Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "User ID phải là số hợp lệ.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Lỗi parse User ID: " + e.getMessage());
            return;
        }

        if (!dateOfBirthStr.isEmpty()) {
            try {
                Date parsedDate = displaySdf.parse(dateOfBirthStr);
                calendar.setTime(parsedDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                dateOfBirth = calendar.getTime();
            } catch (java.text.ParseException e) {
                Toast.makeText(this, "Ngày sinh không hợp lệ. Định dạng: YYYY-MM-DD", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Lỗi parse DateOfBirth: " + e.getMessage());
                return;
            }
        }

        if (!createDateStr.isEmpty()) {
            try {
                createDate = apiSdf.parse(createDateStr);
            } catch (java.text.ParseException e) {
                Toast.makeText(this, "Ngày tạo không hợp lệ. Vui lòng kiểm tra lại.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Lỗi parse CreateDate (readonly): " + e.getMessage());
                return;
            }
        }

        if (userId == null) {
            Toast.makeText(this, "User ID không được để trống.", Toast.LENGTH_LONG).show();
            return;
        }

        SaleUserResponseDto userUpdateDto = new SaleUserResponseDto(
                userId,
                roleId,
                email,
                password,
                phone.isEmpty() ? null : phone,
                userName.isEmpty() ? null : userName,
                dateOfBirth,
                address.isEmpty() ? null : address,
                createDate,
                status,
                isDelete
        );

        Log.d(TAG, "Đang gửi UserUpdateDto: " + userUpdateDto.toString());

        saleApiService.updateUser(userId, userUpdateDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SaleEditUserActivity.this, "Cập nhật người dùng thành công!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = "Lỗi khi cập nhật người dùng: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            String rawErrorBody = response.errorBody().string();
                            errorMsg += " - Chi tiết: " + rawErrorBody;
                            Log.e(TAG, "Lỗi phản hồi API (" + response.code() + ") khi cập nhật: " + rawErrorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi đọc errorBody khi cập nhật: " + e.getMessage(), e);
                        errorMsg += " - Không thể đọc chi tiết lỗi.";
                    }
                    Toast.makeText(SaleEditUserActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối máy chủ khi cập nhật:", t);
                Toast.makeText(SaleEditUserActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
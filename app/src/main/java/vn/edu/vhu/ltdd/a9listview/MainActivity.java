package vn.edu.vhu.ltdd.a9listview;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A9_241A310088";

    private final List<MonAn> danhSachGoc = new ArrayList<>();
    private final List<MonAn> danhSachHienThi = new ArrayList<>();
    private MonAnAdapter adapter;

    private ListView lvMonAn;
    private TextView tvTrong;
    private EditText edtTen, edtGia, edtTimKiem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        lvMonAn = findViewById(R.id.lvMonAn);
        tvTrong = findViewById(R.id.tvTrong);
        edtTen = findViewById(R.id.edtTen);
        edtGia = findViewById(R.id.edtGia);
        edtTimKiem = findViewById(R.id.edtTimKiem);
        Button btnThem = findViewById(R.id.btnThem);

        taoDuLieuMau();

        // Khởi tạo adapter với danh sách hiển thị
        danhSachHienThi.addAll(danhSachGoc);
        adapter = new MonAnAdapter(this, danhSachHienThi);
        lvMonAn.setAdapter(adapter);
        lvMonAn.setEmptyView(tvTrong);

        // --- NÂNG CAO NC1: Lọc danh sách theo thời gian thực khi gõ ô tìm kiếm ---
        edtTimKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locDanhSach(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sự kiện click chọn món
        lvMonAn.setOnItemClickListener((parent, view, position, id) -> {
            MonAn mon = (MonAn) parent.getItemAtPosition(position);
            Toast.makeText(this, getString(R.string.chon_mon, mon.getTen()), Toast.LENGTH_SHORT).show();
        });

        // Sự kiện giữ lâu để xóa món
        lvMonAn.setOnItemLongClickListener((parent, view, position, id) -> {
            MonAn mon = danhSachHienThi.get(position);
            danhSachGoc.remove(mon); // Xóa khỏi danh sách gốc
            danhSachHienThi.remove(position); // Xóa khỏi danh sách hiển thị
            adapter.notifyDataSetChanged();
            Toast.makeText(this, getString(R.string.da_xoa, mon.getTen()), Toast.LENGTH_SHORT).show();
            return true;
        });

        btnThem.setOnClickListener(v -> themMon());
    }

    private void taoDuLieuMau() {
        // Đưa các món bạn vừa cung cấp vào dữ liệu mẫu
        danhSachGoc.add(new MonAn("Nem rán giòn rụm", "Kèm nước chấm chua ngọt", 40000, R.drawable.mon1));
        danhSachGoc.add(new MonAn("Thịt kho tàu đậm đà", "Ba rọi mềm rụm, trứng vịt", 55000, R.drawable.mon2)); // Giá > 45k sẽ đổi màu nền (NC2)
        danhSachGoc.add(new MonAn("Kem tươi nhiều vị", "Chocolate, vani, dâu tây", 35000, R.drawable.mon3));
        danhSachGoc.add(new MonAn("Bánh xèo miền Tây", "Nhân tôm thịt, giá đỗ tươi", 50000, R.drawable.mon4)); // Giá > 45k sẽ đổi màu nền (NC2)
        danhSachGoc.add(new MonAn("Rau muống xào tỏi", "Xanh mướt, dậy mùi tỏi phi", 25000, R.drawable.mon5));
    }

    private void locDanhSach(String tuKhoa) {
        danhSachHienThi.clear();
        if (tuKhoa.isEmpty()) {
            danhSachHienThi.addAll(danhSachGoc);
        } else {
            for (MonAn mon : danhSachGoc) {
                if (mon.getTen().toLowerCase().contains(tuKhoa.toLowerCase())) {
                    danhSachHienThi.add(mon);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void themMon() {
        String ten = edtTen.getText().toString().trim();
        String giaStr = edtGia.getText().toString().trim();

        if (ten.isEmpty()) {
            edtTen.setError(getString(R.string.err_ten));
            edtTen.requestFocus();
            return;
        }
        int gia;
        try {
            gia = Integer.parseInt(giaStr);
        } catch (NumberFormatException e) {
            edtGia.setError(getString(R.string.err_gia));
            edtGia.requestFocus();
            return;
        }
        if (gia <= 0) {
            edtGia.setError(getString(R.string.err_gia_duong));
            edtGia.requestFocus();
            return;
        }

        MonAn monMoi = new MonAn(ten, getString(R.string.moi_them), gia, R.drawable.mon1);
        danhSachGoc.add(0, monMoi);
        locDanhSach(edtTimKiem.getText().toString().trim()); // Cập nhật lại khung nhìn hiển thị

        edtTen.setText("");
        edtGia.setText("");
        edtTen.requestFocus();
        Log.d(TAG, "Đã thêm món: " + ten);
    }
}
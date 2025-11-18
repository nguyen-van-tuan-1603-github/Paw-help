package com.example.paw_help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TrangDoiNguActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTeam;
    private TeamAdapter teamAdapter;
    private List<TeamMember> teamMembers;
    private ImageView btnBack;
    private TextView tvTeamCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_doi_ngu);

        initViews();
        setupRecyclerView();
        loadTeamMembers();
    }

    private void initViews() {
        recyclerViewTeam = findViewById(R.id.recyclerViewTeam);
        btnBack = findViewById(R.id.btnBack);
        tvTeamCount = findViewById(R.id.tvTeamCount);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerViewTeam.setLayoutManager(new LinearLayoutManager(this));
        teamMembers = new ArrayList<>();
        teamAdapter = new TeamAdapter(this, teamMembers);
        recyclerViewTeam.setAdapter(teamAdapter);
    }

    private void loadTeamMembers() {
        // Dữ liệu đội ngũ - có thể thay thế bằng dữ liệu từ database
        teamMembers.clear();

        teamMembers.add(new TeamMember(
                "Phạm Khánh Quynh",
                "Người sáng lập",
                "Chuyên gia phúc lợi động vật",
                "Với hơn 10 năm kinh nghiệm trong lĩnh vực cứu hộ và bảo vệ động vật, " +
                "đã thành lập và điều hành Paw Help từ năm 2013.",
                "PETADOPTTEAM",
                "quynh.pham@pawhelp.com",
                "0912345678",
                R.drawable.quynh
        ));

        teamMembers.add(new TeamMember(
                "Nguyễn Thanh Hiền",
                "Phó giám đốc",
                "Quản lý vận hành",
                "Chuyên viên quản lý các hoạt động cứu hộ và chăm sóc động vật. " +
                "Tham gia đội ngũ từ năm 2015 với nhiệt huyết và tâm huyết.",
                "RESCUETEAM",
                "hien.nguyen@pawhelp.com",
                "0923456789",
                R.drawable.hien
        ));

        teamMembers.add(new TeamMember(
                "Phạm Hồng Hạnh",
                "Tình nguyện viên",
                "Chuyên viên truyền thông",
                "Phụ trách các hoạt động truyền thông và nâng cao nhận thức cộng đồng " +
                "về việc bảo vệ động vật.",
                "MEDIATEM",
                "hanh.pham@pawhelp.com",
                "0934567890",
                R.drawable.hanh
        ));

        teamMembers.add(new TeamMember(
                "Trần Văn Nam",
                "Tình nguyện viên",
                "Bác sĩ thú y",
                "Chăm sóc sức khỏe và điều trị cho các thú cưng bị bỏ rơi và bị thương. " +
                "Có bằng thú y từ Đại học Nông Lâm.",
                "MEDICALTEAM",
                "nam.tran@pawhelp.com",
                "0945678901",
                R.drawable.tuyen
        ));

        teamMembers.add(new TeamMember(
                "Lê Thị Mai",
                "Tình nguyện viên",
                "Điều phối viên",
                "Điều phối các hoạt động cứu hộ và kết nối giữa người cần giúp đỡ " +
                "với đội ngũ tình nguyện viên.",
                "RESCUETEAM",
                "mai.le@pawhelp.com",
                "0956789012",
                R.drawable.mai
        ));

        teamMembers.add(new TeamMember(
                "Hoàng Minh Tuấn",
                "Tình nguyện viên",
                "Chuyên viên IT",
                "Phát triển và bảo trì hệ thống ứng dụng Paw Help, đảm bảo hoạt động " +
                "trơn tru cho cộng đồng.",
                "TECHTEAM",
                "tuan.hoang@pawhelp.com",
                "0967890123",
                R.drawable.teenage
        ));

        teamAdapter.notifyDataSetChanged();

        // Update team count
        if (tvTeamCount != null) {
            tvTeamCount.setText(teamMembers.size() + " thành viên");
        }
    }
}


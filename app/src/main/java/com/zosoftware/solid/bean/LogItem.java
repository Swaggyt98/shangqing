package com.zosoftware.solid.bean;
//这段代码的主要用途是创建一个 LogItem 对象，用于存储和处理各种用户或事件的详细信息。通过 toString 方法，可以方便地输出对象的所有字段信息，有助于调试和日志记录。

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.sql.Timestamp;
 
public class LogItem  {

    public String username = "";
    public String userid;
    public String bloodtype;
    public String gender;
    public String duties;
    public String department;
    public String srank;
    public boolean is_war_wound = false;
    public boolean setrecheck = false;
    public int current_disposal_method = 0;
    public int GCS_score = 3 ;
    public int PHI_score = 0 ;
    public String anti_infection = "";
    public String anti_shock = "";
    public String evacuation_plan = "";
    public String disposal_plan = "";
    public String emergency_surgery = "";
    public String expert_treatment_recommendations = "";
    public String temp = "";
    public String sbpressure = ""; // 收缩压
    public String dbpressure = ""; // 舒张压
    public String hr = ""; // 心率
    public String npb = ""; // npb
    public String bo = ""; // 血氧
    public String bp = ""; //呼吸率
    public Float bp_float = 0f; //呼吸率
    public String tag_color = "绿色"; //伤员标记的颜色
    public String age = ""; // 血氧
    public float ecg = 0;
    public String ecgnum = "";

    public String injured_area_str = "";
    public String injured_ser_str = "";
    public String injured_type_str = "";
    public String comprehan_str = "";

    public int zhengyan_ability = 0;
    public int language_ability = 0;
    public int sport_ability = 0;
    public boolean is_xiongbu_guanchuan = true;
    public boolean is_able_to_walk = true;
    public boolean is_able_to_breath = true;
    public boolean is_breath_la_30 = true;
    public boolean is_qidao_breath = true;
    public boolean is_maoxichongying_lg_2 = true;
    public int mind_state = 0;
    public boolean mind_clear = true;
    public String injured_severity = "轻伤"; // 0 轻伤，1 中伤，2重伤，3危重



    @Override
    public String toString() {
        return "LogItem{" +
                "username='" + username + '\'' +
                ", userid='" + userid + '\'' +
                ", bloodtype='" + bloodtype + '\'' +
                ", gender='" + gender + '\'' +
                ", duties='" + duties + '\'' +
                ", department='" + department + '\'' +
                ", srank='" + srank + '\'' +
                ", is_war_wound=" + is_war_wound +
                ", current_disposal_method=" + current_disposal_method +
                ", GCS_score=" + GCS_score +
                ", PHI_score=" + PHI_score +
                ", anti_infection='" + anti_infection + '\'' +
                ", anti_shock='" + anti_shock + '\'' +
                ", evacuation_plan='" + evacuation_plan + '\'' +
                ", disposal_plan='" + disposal_plan + '\'' +
                ", emergency_surgery='" + emergency_surgery + '\'' +
                ", expert_treatment_recommendations='" + expert_treatment_recommendations + '\'' +
                ", temp='" + temp + '\'' +
                ", sbpressure='" + sbpressure + '\'' +
                ", dbpressure='" + dbpressure + '\'' +
                ", hr='" + hr + '\'' +
                ", npb='" + npb + '\'' +
                ", bo='" + bo + '\'' +
                ", bp='" + bp + '\'' +
                ", bp_float=" + bp_float +
                ", tag_color='" + tag_color + '\'' +
                ", age='" + age + '\'' +
                ", ecg=" + ecg +
                ", ecgnum='" + ecgnum + '\'' +
                ", injured_area_str='" + injured_area_str + '\'' +
                ", injured_ser_str='" + injured_ser_str + '\'' +
                ", injured_type_str='" + injured_type_str + '\'' +
                ", comprehan_str='" + comprehan_str + '\'' +
                ", zhengyan_ability=" + zhengyan_ability +
                ", language_ability=" + language_ability +
                ", sport_ability=" + sport_ability +
                ", is_xiongbu_guanchuan=" + is_xiongbu_guanchuan +
                ", is_able_to_walk=" + is_able_to_walk +
                ", is_able_to_breath=" + is_able_to_breath +
                ", is_breath_la_30=" + is_breath_la_30 +
                ", is_qidao_breath=" + is_qidao_breath +
                ", is_maoxichongying_lg_2=" + is_maoxichongying_lg_2 +
                ", mind_state=" + mind_state +
                ", mind_clear=" + mind_clear +
                ", injured_severity='" + injured_severity + '\'' +
                '}';
    }
}


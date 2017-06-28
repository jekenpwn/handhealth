package jeken.com.handhealth.entity.net;


import jeken.com.handhealth.entity.health.Tb_CountFoot;
import jeken.com.handhealth.entity.health.Tb_Health;
/**
 * Created by Administrator on 2017-06-28.
 */

public interface NetHttp {
    void httpGetUploadHealth(Tb_Health health);
    void httpGetUploadCountfoot(Tb_CountFoot countFoot);
    Tb_Health httpGetDownHealth();
    Tb_CountFoot httpGetDownCountfoot();
}

package kr.co.automart.jewelry.service;

import java.util.Map;

public interface NiceCertService {
    Map<String, String> selectMemberByCI(Map<String, Object> row);

    int addMemberInfo(Map<String, Object> row);

    int updateMemberInfo(Map<String, Object> row);

    String getCI(String sessionId);

    Boolean checkDuplicateId(String memberId);

    int delTempMember(String sessionId);

    int changeMemberInfo(Map<String, Object> row);
}

package kr.co.automart.jewelry.service.impl;

import kr.co.automart.jewelry.service.NiceCertService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NiceCertServiceImpl implements NiceCertService {

    @Autowired
    @Qualifier("sqlSession")
    private SqlSessionTemplate sqlSession;

    @Override
    public Map<String, String> selectMemberByCI(Map<String, Object> row) {
        return sqlSession.selectOne("getMemberInfo", row);
    }

    @Override
    public int addMemberInfo(Map<String, Object> row) {
        return sqlSession.insert("addMemberInfo", row);
    }

    @Override
    public int updateMemberInfo(Map<String, Object> row) {
        return sqlSession.update("updateMemberInfo", row);
    }

    @Override
    public String getCI(String sessionId) {
        return sqlSession.selectOne("getCI", sessionId);
    }

    @Override
    public Boolean checkDuplicateId(String memberId) {
        int count = sqlSession.selectOne("checkDuplicateId", memberId);
        if(count > 0) return true;
        return false;
    }

    @Override
    public int delTempMember(String sessionId) {
        return sqlSession.delete("delTempMember", sessionId);
    }

    @Override
    public int changeMemberInfo(Map<String, Object> row) {
        return sqlSession.update("changeMemberInfo", row);
    }
}

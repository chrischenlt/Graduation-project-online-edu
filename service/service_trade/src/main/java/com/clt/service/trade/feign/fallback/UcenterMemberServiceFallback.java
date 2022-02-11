package com.clt.service.trade.feign.fallback;

import com.clt.service.base.dto.MemberDto;
import com.clt.service.trade.feign.UcenterMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author 陈力天
 * @Date 2022/2/10
 */
@Service
@Slf4j
public class UcenterMemberServiceFallback implements UcenterMemberService {
    @Override
    public MemberDto getMemberDtoByMemberId(String memberId) {
        log.error("远程服务异常");
        return null;
    }
}

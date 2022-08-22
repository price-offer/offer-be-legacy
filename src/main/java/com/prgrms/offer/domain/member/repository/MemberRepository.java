package com.prgrms.offer.domain.member.repository;


import com.prgrms.offer.domain.member.model.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findById(Long id);

    Optional<Member> findByOauthTypeAndOauthId(String oauthType, Long oauthId);

    boolean existsByOauthTypeAndOauthId(String oauthType, Long oauthId);

    boolean existsByNickname(String nickname);

    default Member getByOauthTypeAndOauthId(String oauthType, Long oauthId) {
        return findByOauthTypeAndOauthId(oauthType, oauthId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    default Member getById(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}

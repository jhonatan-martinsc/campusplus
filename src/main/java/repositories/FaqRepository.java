package repositories;

import entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository  extends JpaRepository <Faq, Long> {



}

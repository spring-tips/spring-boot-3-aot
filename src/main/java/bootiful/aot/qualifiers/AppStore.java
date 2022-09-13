package bootiful.aot.qualifiers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("apple")
class AppStore implements Market {

}

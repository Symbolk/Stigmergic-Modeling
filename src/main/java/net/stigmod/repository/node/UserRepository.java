package net.stigmod.repository.node;

//import net.stigmod.domain.Rating;
import net.stigmod.domain.node.User;
//import org.springframework.data.neo4j.annotation.Query;
import net.stigmod.service.StigmodUserDetailsService;
import org.springframework.data.neo4j.repository.GraphRepository;


public interface UserRepository extends GraphRepository<User>, StigmodUserDetailsService {

    User findByMail(String mail);

//    @Query("MATCH (movie:Movie)<-[r:RATED]-(user) where ID(movie)={0} AND ID(user)={1} RETURN r")
//    Rating findUsersRatingForMovie(long movieId, long userId);
}

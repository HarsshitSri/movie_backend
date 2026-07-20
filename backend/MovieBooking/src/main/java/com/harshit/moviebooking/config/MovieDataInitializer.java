package com.harshit.moviebooking.config;

import com.harshit.moviebooking.entity.Movie;
import com.harshit.moviebooking.enums.ContentRating;
import com.harshit.moviebooking.repository.MovieRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(2)
public class MovieDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MovieDataInitializer.class);

    private final MovieRepo movieRepository;

    public MovieDataInitializer(MovieRepo movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        LocalDateTime now = LocalDateTime.now();
        List<Movie> demos = demoMovies(now);

        List<Movie> toInsert = new ArrayList<>();
        for (Movie movie : demos) {
            if (!movieRepository.existsByTitleIgnoreCase(movie.getTitle())) {
                toInsert.add(movie);
            }
        }

        if (toInsert.isEmpty()) {
            log.info("Demo movie catalog already complete ({} titles)", demos.size());
            return;
        }

        movieRepository.saveAll(toInsert);
        log.info("Seeded {} demo movie(s); catalog target is {}", toInsert.size(), demos.size());
    }

    private static List<Movie> demoMovies(LocalDateTime now) {
        return List.of(
                movie("Inception",
                        "A thief who steals corporate secrets through dream-sharing technology is given a chance at redemption.",
                        LocalDate.of(2010, 7, 16), 148, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/inception/300/450", now),
                movie("The Dark Knight",
                        "Batman faces the Joker, a criminal mastermind who plunges Gotham into chaos.",
                        LocalDate.of(2008, 7, 18), 152, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/darkknight/300/450", now),
                movie("Interstellar",
                        "A team of explorers travels through a wormhole in space in an attempt to ensure humanity's survival.",
                        LocalDate.of(2014, 11, 7), 169, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/interstellar/300/450", now),
                movie("Spirited Away",
                        "A young girl enters a world of spirits ruled by gods, witches, and strange creatures.",
                        LocalDate.of(2001, 7, 20), 125, "Japanese", "Japan", ContentRating.PG,
                        "https://picsum.photos/seed/spirited/300/450", now),
                movie("Parasite",
                        "A poor family schemes to become employed by a wealthy household, with unexpected consequences.",
                        LocalDate.of(2019, 5, 30), 132, "Korean", "South Korea", ContentRating.R,
                        "https://picsum.photos/seed/parasite/300/450", now),
                movie("The Matrix",
                        "A computer hacker learns about the true nature of reality and his role in the war against its controllers.",
                        LocalDate.of(1999, 3, 31), 136, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/matrix/300/450", now),
                movie("Amélie",
                        "A shy waitress decides to change the lives of those around her for the better.",
                        LocalDate.of(2001, 4, 25), 122, "French", "France", ContentRating.R,
                        "https://picsum.photos/seed/amelie/300/450", now),
                movie("Mad Max: Fury Road",
                        "In a post-apocalyptic wasteland, Max joins Furiosa on a war-rig escape from a tyrannical warlord.",
                        LocalDate.of(2015, 5, 15), 120, "English", "Australia", ContentRating.R,
                        "https://picsum.photos/seed/furyroad/300/450", now),
                movie("Coco",
                        "A boy who dreams of becoming a musician embarks on a journey to the Land of the Dead.",
                        LocalDate.of(2017, 11, 22), 105, "English", "USA", ContentRating.PG,
                        "https://picsum.photos/seed/coco/300/450", now),
                movie("Whiplash",
                        "A promising young drummer enrolls at a cutthroat music conservatory under an abusive instructor.",
                        LocalDate.of(2014, 10, 10), 106, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/whiplash/300/450", now),
                movie("The Godfather",
                        "An organized-crime dynasty's aging patriarch transfers control to his reluctant son.",
                        LocalDate.of(1972, 3, 24), 175, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/godfather/300/450", now),
                movie("Pulp Fiction",
                        "The lives of two mob hitmen, a boxer, and a gangster's wife intertwine in Los Angeles.",
                        LocalDate.of(1994, 10, 14), 154, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/pulpfiction/300/450", now),
                movie("Forrest Gump",
                        "The presidencies and culture of decades unfold through the eyes of an Alabama man.",
                        LocalDate.of(1994, 7, 6), 142, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/forrest/300/450", now),
                movie("The Shawshank Redemption",
                        "Two imprisoned men bond over years, finding solace and eventual redemption through acts of decency.",
                        LocalDate.of(1994, 9, 23), 142, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/shawshank/300/450", now),
                movie("Gladiator",
                        "A former Roman General sets out to exact vengeance against the corrupt emperor who murdered his family.",
                        LocalDate.of(2000, 5, 5), 155, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/gladiator/300/450", now),
                movie("The Prestige",
                        "Two rival magicians in Victorian London engage in a bitter battle for supremacy.",
                        LocalDate.of(2006, 10, 20), 130, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/prestige/300/450", now),
                movie("Arrival",
                        "A linguist works with the military to communicate with alien visitors after twelve mysterious crafts appear.",
                        LocalDate.of(2016, 11, 11), 116, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/arrival/300/450", now),
                movie("Blade Runner 2049",
                        "A young blade runner discovers a secret that could plunge what's left of society into chaos.",
                        LocalDate.of(2017, 10, 6), 164, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/bladerunner/300/450", now),
                movie("La La Land",
                        "A jazz pianist falls for an aspiring actress while both struggle to make ends meet in Los Angeles.",
                        LocalDate.of(2016, 12, 9), 128, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/lalaland/300/450", now),
                movie("Get Out",
                        "A young African-American visits his white girlfriend's family estate, uncovering a disturbing secret.",
                        LocalDate.of(2017, 2, 24), 104, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/getout/300/450", now),
                movie("Everything Everywhere All at Once",
                        "An aging Chinese immigrant is swept up in an insane adventure where she alone can save existence.",
                        LocalDate.of(2022, 3, 25), 139, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/eeaao/300/450", now),
                movie("Dune",
                        "Paul Atreides leads nomadic tribes in a battle for control of the desert planet Arrakis.",
                        LocalDate.of(2021, 10, 22), 155, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/dune/300/450", now),
                movie("The Grand Budapest Hotel",
                        "A legendary concierge and his lobby boy become involved in a murder mystery and a family fortune.",
                        LocalDate.of(2014, 3, 28), 99, "English", "Germany", ContentRating.R,
                        "https://picsum.photos/seed/budapest/300/450", now),
                movie("Pan's Labyrinth",
                        "In Franco-era Spain, a girl escapes into a fairy-tale underworld while war rages above.",
                        LocalDate.of(2006, 10, 11), 118, "Spanish", "Spain", ContentRating.R,
                        "https://picsum.photos/seed/panslabyrinth/300/450", now),
                movie("Your Name",
                        "Two teenagers share a profound connection after they start swapping bodies mysteriously.",
                        LocalDate.of(2016, 8, 26), 106, "Japanese", "Japan", ContentRating.PG,
                        "https://picsum.photos/seed/yourname/300/450", now),
                movie("The Social Network",
                        "Harvard student Mark Zuckerberg creates Facebook and faces lawsuits and fractured friendships.",
                        LocalDate.of(2010, 10, 1), 120, "English", "USA", ContentRating.PG_13,
                        "https://picsum.photos/seed/socialnetwork/300/450", now),
                movie("No Country for Old Men",
                        "Violence and mayhem ensue after a hunter stumbles upon a drug deal gone wrong.",
                        LocalDate.of(2007, 11, 21), 122, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/nocountry/300/450", now),
                movie("Her",
                        "A lonely writer develops an unlikely relationship with an operating system designed to meet his needs.",
                        LocalDate.of(2013, 12, 18), 126, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/her/300/450", now),
                movie("Spider-Man: Into the Spider-Verse",
                        "Teen Miles Morales becomes Spider-Man and joins other Spider-People from across the multiverse.",
                        LocalDate.of(2018, 12, 14), 117, "English", "USA", ContentRating.PG,
                        "https://picsum.photos/seed/spiderverse/300/450", now),
                movie("Oppenheimer",
                        "The story of J. Robert Oppenheimer and his role in the development of the atomic bomb.",
                        LocalDate.of(2023, 7, 21), 180, "English", "USA", ContentRating.R,
                        "https://picsum.photos/seed/oppenheimer/300/450", now)
        );
    }

    private static Movie movie(String title,
                               String synopsis,
                               LocalDate releaseDate,
                               int runtimeMinutes,
                               String language,
                               String country,
                               ContentRating contentRating,
                               String posterUrl,
                               LocalDateTime now) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setSynopsis(synopsis);
        movie.setReleaseDate(releaseDate);
        movie.setRuntimeMinutes(runtimeMinutes);
        movie.setLanguage(language);
        movie.setCountryOfOrigin(country);
        movie.setContentRating(contentRating);
        movie.setPosterUrl(posterUrl);
        movie.setAverageRating(BigDecimal.ZERO);
        movie.setRatingCount(0);
        movie.setCreatedAt(now);
        movie.setUpdatedAt(now);
        return movie;
    }
}

package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.exceptions.UserNotFoundException;
import tourGuide.model.user.User;
import tourGuide.model.user.UserPreferences;
import tourGuide.model.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.List;

import static tourGuide.service.TourGuideServiceImpl.tripPricerApiKey;

@Service
public class TripPricerServiceImpl implements TripPricerService {

    @Autowired
    TourGuideService tourGuideService;

    private final TripPricer tripPricer = new TripPricer();

    /**
     * Get a list of provider with price offer by user.
     * Each provider contains :
     * - a name
     * - a price
     * - an id
     *
     * @param user the user whose providers are sought
     * @return a list of providers with price offer
     */
    @Override
    public List<Provider> getTripDeals(User user) {
        if (!tourGuideService.isExistingUser(user)) throw new UserNotFoundException("No user found with this username");
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
        List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
        user.setTripDeals(providers);
        return providers;
    }

    /**
     * Update user's preferences
     *
     * @param user the user whose preferences are to be updated
     * @param userPreferences the preferences to update
     * @return updated user preferences
     */
    @Override
    public UserPreferences updateUserPreferences(User user, UserPreferences userPreferences) {
        if (!tourGuideService.isExistingUser(user)) throw new UserNotFoundException("No user found with this username");
        user.setUserPreferences(userPreferences);
        return user.getUserPreferences();
    }

}
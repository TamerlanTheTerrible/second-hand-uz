package me.timur.secondhanduz.user.application.service;

import java.util.List;

/**
 * Anti-cycle port: provides review ratings from the review module without creating
 * a circular dependency between the user and review modules.
 */
public interface ReviewAverageProvider {

    /** Return all numeric ratings given to the specified user. */
    List<Double> getRatingsForUser(Long userId);
}

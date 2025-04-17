package com.matchme.srv.repository;

import java.util.List;

import com.matchme.srv.model.connection.DatingPool;

public interface MatchingRepositoryCustom {

  List<DatingPool> findPotentialMatchesDynamically(DatingPool searchingUser);
}

package com.alvaroquintana.usecases

import com.alvaroquintana.data.repository.RankingRepository
import com.alvaroquintana.domain.User

class SaveTopScore(private val rankingRepository: RankingRepository) {

    suspend fun invoke(user: User): Result<User> = rankingRepository.addRecord(user)

}

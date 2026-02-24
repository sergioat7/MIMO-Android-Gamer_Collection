package es.upsa.mimo.gamercollection.domain

import es.upsa.mimo.gamercollection.domain.model.ErrorModel
import es.upsa.mimo.gamercollection.domain.model.Game
import es.upsa.mimo.gamercollection.domain.model.Saga
import es.upsa.mimo.gamercollection.domain.model.Song
import es.upsa.mimo.gamercollection.models.ErrorResponse
import es.upsa.mimo.gamercollection.models.GameResponse
import es.upsa.mimo.gamercollection.models.SagaResponse
import es.upsa.mimo.gamercollection.models.SongResponse

fun GameResponse.toDomain(): Game = Game(
    id = id,
    name = name,
    platform = platform,
    score = score,
    pegi = pegi,
    distributor = distributor,
    developer = developer,
    players = players,
    releaseDate = releaseDate,
    goty = goty,
    format = format,
    genre = genre,
    state = state,
    purchaseDate = purchaseDate,
    purchaseLocation = purchaseLocation,
    price = price,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    loanedTo = loanedTo,
    observations = observations,
    saga = saga?.toDomain(),
    songs = songs.map { it.toDomain() },
)

fun SagaResponse.toDomain(): Saga = Saga(
    id = id,
    name = name,
    games = games.map { it.toDomain() },
)

fun SongResponse.toDomain(): Song = Song(
    id = id,
    name = name,
    singer = singer,
    url = url,
)

fun Game.toRemoteData(): GameResponse = GameResponse(
    id = id,
    name = name,
    platform = platform,
    score = score,
    pegi = pegi,
    distributor = distributor,
    developer = developer,
    players = players,
    releaseDate = releaseDate,
    goty = goty,
    format = format,
    genre = genre,
    state = state,
    purchaseDate = purchaseDate,
    purchaseLocation = purchaseLocation,
    price = price,
    imageUrl = imageUrl,
    videoUrl = videoUrl,
    loanedTo = loanedTo,
    observations = observations,
    saga = saga?.toRemoteData(),
    songs = songs.map { it.toRemoteData() }.toMutableList(),
)

fun Saga.toRemoteData(): SagaResponse = SagaResponse(
    id = id,
    name = name,
    games = games.map { it.toRemoteData() },
)

fun Song.toRemoteData(): SongResponse = SongResponse(
    id = id,
    name = name,
    singer = singer,
    url = url,
)

fun ErrorResponse.toDomain(): ErrorModel = ErrorModel(
    error = error,
    errorKey = errorKey
)
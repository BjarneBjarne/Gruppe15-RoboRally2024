// package com.group15.roborally.server.controller;

// import com.group15.roborally.server.model.Market;
// import com.group15.roborally.server.repository.GameRepository;
// import com.group15.roborally.server.repository.MarketRepository;
// import com.group15.roborally.server.repository.PlayerRepository;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/market")

// public class UpgradeController {

//     PlayerRepository playerRepository;
//     GameRepository gameRepository;
//     MarketRepository markRepository;

//     public UpgradeController(PlayerRepository playerRepository, GameRepository gameRepository, MarketRepository markRepository) {
//         this.playerRepository = playerRepository;
//         this.gameRepository = gameRepository;
//         this.markRepository = markRepository;
//     }
    

//     /**
//      * Endpoint to get the priority of who will upgrade next, in a game
//      * 
//      * @author  Marcus Rémi Lemser Eychenne, s230985
//      * 
//      * @param gameId - the id of the game
//      * 
//      * @return ResponseEntity<Integer> - the priority number of who will upgrade next
//      */
//     @GetMapping(value = "/{gameId}/turn", consumes = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<Integer> getTurn(@PathVariable("gameId") long gameId) {
//         return ResponseEntity.ok().body(markRepository.findById(gameId).orElse(null).getTurn());
//     }

//     /**
//      * Endpoint to get the market of a game
//      * 
//      * @author  Marcus Rémi Lemser Eychenne, s230985
//      * 
//      * @param gameId - the id of the game
//      * 
//      * @return ResponseEntity<Market> - the market of the game
//      */
//     @GetMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<String[]> getMarket(@PathVariable("gameId") long gameId) {
//         Market market = markRepository.findById(gameId).orElse(null);
//         String[] marketCards = market.getCards();
//         return ResponseEntity.ok().body(marketCards);
//     }

//     /**
//      * Endpoint to update the market of a game
//      * 
//      * @param marketCards - the cards to be updated in the market
//      * 
//      * @param gameId - the id of the game
//      * 
//      * @return ResponseEntity<String> - a message indicating the success of the operation
//      */
//     @PutMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
//     public ResponseEntity<String> postMarket(@RequestBody String[] marketCards, @PathVariable("gameId") long gameId) {
//         Market market = markRepository.findById(gameId).orElse(null);
//         market.setCards(marketCards); 
//         markRepository.save(market);
//         return ResponseEntity.ok().build();
//     }
// }

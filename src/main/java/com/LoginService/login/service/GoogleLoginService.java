package com.LoginService.login.service;

import com.LoginService.login.entity.TokenHeader;
import com.LoginService.login.entity.User;
import com.LoginService.login.entity.request.GetTokenRequestEntity;
import com.LoginService.login.entity.response.GetTokenResponseEntity;
import com.LoginService.login.entity.response.PublicKeyEntity;
import com.LoginService.login.entity.response.PublicKeyListResponseEntity;
import com.LoginService.login.enums.UserProviderEnum;
import com.LoginService.login.repository.UsersRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Service
public class GoogleLoginService {

    @Value("${spring.google.client.id}")
    private String CLIENT_ID;

    @Value("${spring.google.client.secrete}")
    private String CLIENT_SECRET;

    @Value("${spring.google.client.redirect.url}")
    private  String REDIRECT_URI;

    @Value("${spring.google.authorize.url}")
    private  String GOOGLE_AUTH_URL;

    @Autowired
    private RestService restService;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired JWTService jwtService;

   public Map<String,String> handleGoogleLoginCallback(String authorizationCode){

      GetTokenResponseEntity tokenResponseEntity =  getJwtTokenFromAuthorizationCode(authorizationCode);

      Claims claims = getClaimsFromToken(tokenResponseEntity.getId_token());

      try {
          User user = getUserFromClaims(claims);
          saveUser(user);
          Map<String, String> tokens = new HashMap<>();
          String jwtToken = jwtService.generateJWTToken(user.getEmail());
          String refreshToken = jwtService.generateRefreshToken(user.getEmail());
          tokens.put("JWT", jwtToken);
          tokens.put("refreshToken",refreshToken);
          return tokens;
      }
      catch (Exception e){
          throw new RuntimeException(e.getMessage());
      }
   }


   private GetTokenResponseEntity getJwtTokenFromAuthorizationCode(String authorizationCode){
       GetTokenRequestEntity requestBody = this.preparePayloadToGetJwtTokenFromAuthCode(authorizationCode);
       var response = restService.post( "https://oauth2.googleapis.com/token",requestBody, GetTokenResponseEntity.class);
       System.out.println("Token received From google..");
       return response;
   }

    private GetTokenRequestEntity preparePayloadToGetJwtTokenFromAuthCode(String authorizationCode){
       GetTokenRequestEntity requestBody = new GetTokenRequestEntity(authorizationCode,CLIENT_ID,CLIENT_SECRET);
       requestBody.setGrant_type("authorization_code");
       requestBody.setRedirect_uri(REDIRECT_URI);
       return requestBody;
    }

    private PublicKeyEntity[] getPublicKeysList(){
      PublicKeyListResponseEntity response=  restService.get("https://www.googleapis.com/oauth2/v3/certs", PublicKeyListResponseEntity.class);
      System.out.println("Public keys List received ");
      return  response.getKeys();
   }

    private Claims getClaimsFromToken(String jwtToken){
       PublicKeyEntity [] publicKeys = this.getPublicKeysList();
       TokenHeader header = this.getTokenHeader(jwtToken);
       PublicKeyEntity publicKeyEntity = Arrays.stream(publicKeys)
               .filter( key -> key.getKid().equals(header.getKid() )
                               && key.getAlg().equals(header.getAlg() )
               ).findFirst().orElse(null);


       PublicKey publicKey = this.getPublicKeyFromModulesAndExponent( Objects.requireNonNull(publicKeyEntity).getN(),publicKeyEntity.getE(),publicKeyEntity.getKty());
       Claims claims = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(jwtToken).getPayload();
       System.out.println("Claims have been verified and extracted the payload");
      return claims;
   }

    private PublicKey getPublicKeyFromModulesAndExponent(String modulus, String exponent, String algorithm){

       if(modulus == null && exponent == null) { return null; }
       byte[] modulusBytes = Base64.getUrlDecoder().decode(modulus);
       byte[] exponentBytes = Base64.getUrlDecoder().decode(exponent);
        System.out.println("Modules and Exponent Taken From keys.");

       // Convert the decoded bytes to BigInteger
       BigInteger BigModulus = new BigInteger(1, modulusBytes);
       BigInteger BigExponent = new BigInteger(1, exponentBytes);

       // Generate RSA public key from modulus and exponent
       RSAPublicKeySpec spec = new RSAPublicKeySpec(BigModulus, BigExponent);

       try {
           KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
           System.out.println("Public keys extracted from modules and exponent");
           return keyFactory.generatePublic(spec);

       } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
           throw new RuntimeException(e);
       }
   }

    private TokenHeader getTokenHeader(String token){
       String[] chunks = token.split("\\.");
       Base64.Decoder decoder = Base64.getUrlDecoder();
       String str= new String (decoder.decode(chunks[0]));
       ObjectMapper objectMapper=  new ObjectMapper();

       try {
           Map<String, Object> header = objectMapper.readValue(str, new TypeReference<>() {
           });
           return new TokenHeader((String) header.get("alg"),(String) header.get("kid"));
       } catch (JsonProcessingException e) {
           throw new RuntimeException("Failed to parse JWT header", e);
       }

   }

    // Construct the authorization URL
   public  String generateAuthorizationUrl(){
       return String.format(
               "%s?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&access_type=offline&prompt=consent",
               GOOGLE_AUTH_URL,
               CLIENT_ID,
               REDIRECT_URI,
               "openid%20email%20profile"
       );
   }

   private User getUserFromClaims(Claims claims) {
       var username = claims.get("name").toString();
       var email = claims.get("email").toString();
       var refreshToken = jwtService.generateRefreshToken(email);
       User user = new User();
       user.setUsername(username);
       user.setEmail(email);
       user.setProvider(UserProviderEnum.GOOGLE);
       user.setRefreshToken(refreshToken);
       return user;
   }

   private void saveUser(User user){

       if(!isUserExists(user.getEmail())){
           usersRepo.save(user);
           System.out.println("User Saved to DB.");
       }else {
           System.out.println("User Already Exists");
       }
   }

    private Boolean isUserExists(String email){
        User user = usersRepo.findByEmail(email);
        return user != null;
    }

}

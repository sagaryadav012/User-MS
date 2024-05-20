package com.lld.userms.services;

import com.lld.userms.Exceptions.*;
import com.lld.userms.models.Token;
import com.lld.userms.models.User;
import com.lld.userms.repositories.TokenRepository;
import com.lld.userms.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final String SECRET_KEY = "8Zz5tw0Ionm3XPZZfN0NOml3z9FMfmpgXwovR9fp6ryDIoGRM8EPHAB6iHsc0fb";

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User signup(String name, String email, String password) throws Exception{
        // 1.Check email exist in db, if yes throw Exception
        // 2.Save user
        Optional<User> user = this.userRepository.findUserByEmail(email);
        if(user.isPresent()){
           throw new UserFoundException("User already registered");
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        String encodedPassword = this.bCryptPasswordEncoder.encode(password);
        newUser.setPassword(encodedPassword);
        return this.userRepository.save(newUser);
    }

    @Override
    public Token login(String email, String password) throws Exception {
        /*
            step 1 : check email exists in db.
            step 2 : check password is valid.
            step 3 : check login devices.
            step 4 : If all three steps succeeded then create and return token.
         */

        User user = this.userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotFoundException("User has not registered yet"));

        String encodedPassword = user.getPassword();
        boolean matches = this.bCryptPasswordEncoder.matches(password, encodedPassword);
        if(!matches) throw new IncorrectPasswordException("Incorrect Password");

        long loginDeviceCount = this.tokenRepository.countActiveTokensByUserId(user);
        if(loginDeviceCount >= 2){
            throw new TooManyDevicesException("Login limit exceeded: You have reached the maximum number of logged-in devices. Please log out from another device to continue.");
        }


        Token token = new Token();
        // Calculate date after 30 days
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        Date sevenDaysLater = c.getTime();

        // Generate token value
        String value = RandomStringUtils.randomAlphanumeric(128);

        token.setUser(user);
        token.setValue(value);
        token.setActive(true);
        token.setExpireAt(sevenDaysLater);

        // JWT
//        String jwtToken = createJWTToken(user);
//        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken).getBody();
//        Object user1 = claims.get("User");
//        Date expiration = claims.getExpiration();


        return this.tokenRepository.save(token);
    }
    public String createJWTToken(User user){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        Date sevenDaysLater = c.getTime();

        // JWT Token
        String jwtToken = Jwts.builder().claim("User",user)
                .setIssuedAt(new Date())
                .setExpiration(sevenDaysLater)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();

       return jwtToken;
    }

    @Override
    public void logout(String value) throws Exception {
        // step1 : Fetch token from db using token value;
        // step2 : If not found throw exception, else we have two options 1. Hard Delete, 2.Soft Delete.
        // Hard Delete deletes token data from db, Soft Delete sets isActive to false.
        // step3 : set isActive to false, and update token in db.

        Token token = this.tokenRepository.findTokenByValue(value).orElseThrow(() -> new InvalidTokenException("Token not found"));
        if(token.isActive()){
            token.setActive(false);
            this.tokenRepository.save(token);
        }
    }

    @Override
    public Token validate_token(String value) throws Exception {
        /*
        1. Fetch the token from db using value (select * from tokens where value = {value})
        2. If token is not present in db, throw exception
        3. Else, check whether the token has expired or not
        4. If token is expired, then throw an Exception
        5. Else you are going to return the token
         */
        Token token = this.tokenRepository.findTokenByValue(value).orElseThrow(() -> new InvalidTokenException("Token not found"));
        Date expireAt = token.getExpireAt();
        Date now = new Date();

        if(now.after(expireAt) || !token.isActive()){
            throw new InvalidTokenException("Toke has expired");
        }
        return token;
    }
}

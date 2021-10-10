package com.example.authserver.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.annotation.PostConstruct;

@EnableAuthorizationServer
@Configuration
public class AuthServerConfigure extends AuthorizationServerConfigurerAdapter {

    private PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthorizationEndpoint authorizationEndpoint;

    private final AuthenticationManager authenticationManager;

    public AuthServerConfigure(AuthenticationManager authenticationManager,
                               @Lazy AuthorizationEndpoint authorizationEndpoint) {//lazy 解决循环依赖的问题
        this.authenticationManager = authenticationManager;
        this.authorizationEndpoint = authorizationEndpoint;
    }

    @PostConstruct
    public void init(){
        authorizationEndpoint.setUserApprovalPage("forward:/oauth/confirm");
        authorizationEndpoint.setErrorPage("forward:/oauth/error");
    }


    /*四种授权方式。

    授权码模式（authorization code）
    密码模式（resource owner password credentials） 此两者均为用户模式的token

    authorization code 是最严谨的， 支付宝和微信api都使用这种模式

    比如微信， 扫码， 然后会将code重定向到第三方的客户端， 第三方客户端再利用此code获取用户的token
    也就是说 扫码- 用户选择是否给第三方客户端授权，只有授权操作后， 改客户端才能使用该用户的凭证去访问微信的资源

    所以，使用google 和 github 登录都会有一个确定的按钮，只有用户确定了， 才能再进一步操作



    简化模式（implicit）

    客户端模式（client credentials） 仅针对client， 即client的token
        针对一些不需要用户参与的接口， 比如获取关注你的人员列表



    resourceId： 此client可访问的资源列表，可以和scope 二选一， 其实他们并么有固定的含义， 具体用法要看自己场景

    scope: 作用域限制，比如微信的scope就是指定一个范围， 比如用户详细信息， 钉钉的部门信息，部门操作等等。 即限制client访问的范围, 在分布式服务中的各微服务实例

    或者：
    scopes，请求资源作用域，用于限制客户端与用户无法访问没有作用域的资源
    resourceIds，可选，资源id，可以对应一个资源服务器，个人理解为某个资源服务器的所有资源标识
    redirectUris，回调地址，有两个作用：1.回调客户端地址，返回授权码； 2.校验是否是同一个客户端

    */

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("open_client")
                .resourceIds("foo")
                .authorizedGrantTypes("authorization_code", "refresh_token", "client_credentials")
                .redirectUris("http://localhost:8083/auth/code")//带上code重定向到资源服务器, 可多个的
                .scopes("A","B","C")
                .authorities("READ")
                .secret(encoder.encode("123456"))

                .and()
                .withClient("resource_client")
                .authorizedGrantTypes("authorization_code","client_credentials", "refresh_token")
                .resourceIds("foo")
                .scopes("A","B","C","D")
                .authorities("READ")
                .redirectUris("http://localhost:8086/login")//带上code重定向到资源服务器, 可多个的
                .secret(encoder.encode("123456")).autoApprove(true)//开启， 这个client不需要到授权页， 因为是内部的产品， 不需要再经过用户确认授权
        ;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        endpoints
                .authenticationManager(authenticationManager) //支持password授权模式
                .tokenStore(new InMemoryTokenStore())
                .reuseRefreshTokens(true);
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        //允许表单认证
        oauthServer.passwordEncoder(encoder);
        oauthServer.allowFormAuthenticationForClients().checkTokenAccess("isAuthenticated()");
    }


}

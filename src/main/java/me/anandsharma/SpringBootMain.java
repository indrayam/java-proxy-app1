package me.anandsharma;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class SpringBootMain implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMain.class, args);
    }

    public static void performMathsOps(IMaths proxiedMath) {
        System.out.println(proxiedMath.getClass().getName());
        System.out.println(proxiedMath.sum(1, 1));
        System.out.println(proxiedMath.sum(1, 2));
        System.out.println(proxiedMath.multiply(1, 2));
    }

    @Override
    public void run(String... args) throws Exception {
        // Type 1: Call method performMathsOps on an actual instance of your Maths class
        Maths theMath = new Maths();
        performMathsOps(theMath);

        // Type 2: Call method performMathsOps on a proxied instance of your Maths class
        // Proxy Method: Java Proxy via Interfaces
        InvocationHandler h = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("Calling method " + method.getName() + " with args  " + Arrays.toString(args));
                return method.invoke(theMath, args);
            }
        };
        IMaths proxiedMath = (IMaths) Proxy.newProxyInstance(SpringBootMain.class.getClassLoader(),
                new Class<?>[]{IMaths.class}, h);
        performMathsOps(proxiedMath);

        // Type 3: Call method performMathsOps on a proxied instance of your Maths class
        // Proxy Method: CGLIB via extending a class
        Callback callback = new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("Calling method " + method.getName() + " with args  " + Arrays.toString(args));
                return method.invoke(theMath, args);
            }
        };
        Maths theXtendedMath = (Maths) Enhancer.create(Maths.class, callback);
        performMathsOps(theXtendedMath);
    }
}

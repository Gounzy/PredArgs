import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Predicate app = Predicate.app();
        Predicate tapp = Predicate.tapp();
        Predicate tapp2 = Predicate.tapp2();

        Analyzer ana = new Analyzer();

        System.out.println(app);
        System.out.println(tapp);
        System.out.println(tapp2);

        ana.addPredicate(app);
        ana.addPredicate(tapp);
        ana.addPredicate(tapp2);

        ana.analyze();
        //ana.displayEnvironment();
    }
}
/*
    app(X, Y, Z) <- X => nil, Z := Y
    app(X, Y, Z) <- X => cons(E, Es), app(Es, Y, Zs), Z <= cons(E, Zs)

    tapp(L1, L2, L3, R) <- app(L1, L2, L12), app(L12, L3, R)

    tapp2(A, B, C, D) <- A => nil, B => nil, D := C
    tapp2(A, B, C, D) <- A => nil, B => cons(B1, B2), tapp2(A, B2, C, D1), D <= cons(B1, D1)
    tapp2(A, B, C, D) <- A => cons(A1, As), tapp2(As, B, C, D1), D <= cons(A1, D1)

*/
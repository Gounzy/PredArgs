import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Predicate app = Predicate.app();
        Predicate tapp = Predicate.tapp();
        Predicate tapp2 = Predicate.tapp2();
        Predicate qapp = Predicate.qapp();
        Predicate qapp2 = Predicate.qapp2();
        Predicate concat = Predicate.concat();

        Analyzer ana = new Analyzer(false);

        System.out.println(app);
        System.out.println(tapp);
        System.out.println(tapp2);
        System.out.println(qapp);
        System.out.println(qapp2);
        System.out.println(concat);

        ana.addPredicate(app);
        ana.addPredicate(tapp);
        ana.addPredicate(tapp2);
        ana.addPredicate(qapp);
        ana.addPredicate(qapp2);
        ana.addPredicate(concat);

        ana.analyze();

        ana.reorderPredicates();
        ana.displayEnvironment();
    }
}
/*
    app(X, Y, Z) <- X => nil, Z := Y.
    app(X, Y, Z) <- X => cons(E, Es), app(Es, Y, Zs), Z <= cons(E, Zs).

    tapp(L1, L2, L3, R) <- app(L1, L2, L12), app(L12, L3, R).

    tapp2(A, B, C, D) <- A => nil, B => nil, D := C.
    tapp2(A, B, C, D) <- A => nil, B => cons(B1, B2), tapp2(A, B2, C, D1), D <= cons(B1, D1).
    tapp2(A, B, C, D) <- A => cons(A1, As), tapp2(As, B, C, D1), D <= cons(A1, D1).

    qapp(F, G, H, I, J) <- app(F, G, FG), app(FG, H, FGH), app(FGH, I, J).

    qapp2(F1, G1, H1, I1, J1) <- concat(G1, F1, FG1), concat(FG1, H1, FGH1), concat(I1, FGH1, J1).

    concat(V1, V2, V3) <- V2 => cons(V21, V2s), concat(V1, V2s, V3s), V3 <= cons(V21, V3s).
    concat(V1, V2, V3) <- V2 => nil, V3 := V1.
*/
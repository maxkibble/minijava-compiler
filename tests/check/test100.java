class test107{
    public static void main(String[] a){
	    System.out.println(new A().start());
    }
}

class A extends C {
    public int start(){
        return a.b(c.d()); //TE
    }
}

class C extends B {
    E f;
    public G start(){
        h[i] = (k[j]).m(n,p,q[l]);
        return 1;
    }
}

class B extends A {
}

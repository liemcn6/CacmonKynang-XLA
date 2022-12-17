
import static java.lang.Math.abs;
import java.util.Scanner;

public class NhanChap {
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        int I[][] = new int[100][100];
        int Hx[][] = new int[100][100];
        int Hy[][] = new int[100][100];
        int Ix[][] = new int[100][100];
        int Iy[][] = new int[100][100];
        int kq[][] = new int[100][100];
        System.out.print("Ma tran cap n = ");
        int n = sc.nextInt();
        
        System.out.println("Nhap ma tran I:");
        for(int i=1; i<= n; i++){
            for(int j=1; j<= n; j++){
                I[i][j] = sc.nextInt();
            }
        }
        
        System.out.println("Nhap ma tran Hx: ");
        for(int i=1; i<= 3; i++){
            for(int j=1; j<= 3; j++){
                Hx[i][j] = sc.nextInt();
            }
        }
        
        System.out.println("Nhap ma tran Hy: ");
        for(int i=1; i<= 3; i++){
            for(int j=1; j<= 3; j++){
                Hy[i][j] = sc.nextInt();
            }
        }
        
        System.out.println("Ma tran Ix:");
        for(int i=1; i<= n; i++){
            for(int j=1; j<= n; j++){
               Ix[i][j] = I[i-1][j-1]*Hx[1][1] +  I[i-1][j]*Hx[1][2] + I[i-1][j+1]*Hx[1][3] + I[i][j-1]*Hx[2][1] 
                       + I[i][j]*Hx[2][2] + I[i][j+1]*Hx[2][3] + I[i+1][j-1]*Hx[3][1] + I[i+1][j]*Hx[3][2] 
                       + I[i+1][j+1]*Hx[3][3] ;
               System.out.print(Ix[i][j] + " ");
                
            }
            System.out.println();
        }   
        
        System.out.println("Ma tran Iy:");
        for(int i=1; i<= n; i++){
            for(int j=1; j<= n; j++){
               Iy[i][j] = I[i-1][j-1]*Hy[1][1] +  I[i-1][j]*Hy[1][2] + I[i-1][j+1]*Hy[1][3] + I[i][j-1]*Hy[2][1] 
                       + I[i][j]*Hy[2][2] + I[i][j+1]*Hy[2][3] + I[i+1][j-1]*Hy[3][1] + I[i+1][j]*Hy[3][2] 
                       + I[i+1][j+1]*Hy[3][3] ;
               System.out.print(Iy[i][j] + " ");
                
            }
            System.out.println();
        }  
        
        System.out.println("Ma tran kq:");
        for(int i=1; i<= n; i++){
            for(int j=1; j<= n; j++){
               kq[i][j] = Math.abs(Ix[i][j]) + Math.abs(Iy[i][j]);
               System.out.print(Iy[i][j] + " ");
                
            }
            System.out.println();
        }         
    }
}

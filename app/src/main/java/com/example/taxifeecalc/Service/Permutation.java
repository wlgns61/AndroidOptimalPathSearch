package com.example.taxifeecalc.Service;

import java.util.ArrayList;

public class Permutation {

    int n;
    public ArrayList<ArrayList<Integer>> permutation = new ArrayList<>();

    public Permutation(int n){
        this.n = n;
    }

    private void backTracking(ArrayList<Integer> arr, Boolean[] visited, int cnt, int N){

        if(cnt == N){
            ArrayList<Integer> tmp = new ArrayList<>(arr);
            this.permutation.add(tmp);
            return;
        }
        for(int i=0;i<N;i++){
            if(visited[i] == false){
                visited[i] = true;
                arr.add(i+1); //출발지가 0이므로 1이상의 숫자만 저장
                backTracking(arr, visited, cnt + 1, N);
                visited[i] = false;
                arr.remove(arr.size() - 1);
            }
        }
    }

    public ArrayList<ArrayList<Integer>> getPermutation(){

        Boolean[] visited = new Boolean[n];
        ArrayList<Integer> arr = new ArrayList<>();

        for(int i=0;i<n;i++)
            visited[i] = false;

        backTracking(arr, visited, 0, n);
        return permutation;
    }
}

package com.example.demo.src.paging;

// 게시판 하단의 페이징을 담당한다.

public class Paging {

    private int page=5; // 현재 게시글 번호
    private int totalCount; // 게시판 전체 게시글 개수

    private int countList = 3; // 게시판 화면에서 한번에 보여질 게시글 개수

    private int countPage = 5; // 한 화면에 출력될 페이지 수 1~5 이런식으로

    private int startPage; // 화면의 시작 번호
    private int endPage;  // 화면의 끝 번호

    private boolean prev; // 페이징 이전 버튼 활성화 여부
    private boolean next; // 페이징 다음 버튼 활성화 여부
    private int totalPage; //총 페이지 수

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        pagingData();
    }

    private void pagingData() {
        totalPage = totalCount / countList;
        if(totalCount % countList > 0){
            totalPage++;
        }
        System.out.println(totalPage+"dfddd");
        if(totalCount < page){
            page = totalCount;
        }

        startPage = ((page-1)/countPage) * countPage + 1;
        endPage = startPage + countPage - 1;
        if(endPage > totalPage){
            endPage = totalPage;
        }

        prev = startPage == 1 ? false : true;
        // 이전 버튼 생성 여부 = 시작 페이지 번호가 1과 같으면 false, 아니면 true
        next = endPage >= totalPage ? false : true;
        // 다음 버튼 생성 여부 = 끝 페이지 번호 * 한 페이지당 보여줄 게시글의 개수가 총 게시글의 수보다
        // 크거나 같으면 false, 아니면 true
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCountList() {
        return countList;
    }

    public void setCountList(int countList) {
        this.countList = countList;
    }

    public int getCountPage() {
        return countPage;
    }

    public void setCountPage(int countPage) {
        this.countPage = countPage;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public boolean isPrev() {
        return prev;
    }

    public void setPrev(boolean prev) {
        this.prev = prev;
    }

    public boolean isNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

//    @Override
//    public String toString() {
//        return "PageMaker [totalCount=" + totalCount + ", startPage=" + startPage + ", endPage=" + endPage + ", prev="
//                + prev + ", next=" + next + ", displayPageNum=" + displayPageNum + ", cri=" + cri + "]";
//    }

}

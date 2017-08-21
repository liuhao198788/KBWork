log：拖动到别的页面，返回原来页面，item时有刷新异常的问题

/*
** 记录原拖动的页面 的页面 pageIndex
*/

 private void executeItemReplaceAction(AppItem sourceItem, AppItem targetItem) {

        //来源item信息
        int sourcePos = sourceItem.itemPos;

        //目标item位置
        int targetPos = targetItem.itemPos;

        Lg.d("sourcePos: " + sourcePos + " targetPos: " + targetPos);

        //add by liuhao 0808
        if(sourcePos/pageSize!=targetPos/pageSize){
            needUpdateDataPageIndex=sourcePos/pageSize;
        }
.........

/*
翻页后，如果翻到原页面，因为recyclerView回收机制，不显示的就回收
所以调用notifyDataSetChanged只会刷新当前显示的，不会刷新回收的页面

所以，需要手动记录翻页到原页面是，手动刷新
*/
public void onPageChange(int index) {
        mIndicator.setOffset(index);

        //add by liuhao 0808
        if(needUpdateDataPageIndex!=-1&&index==needUpdateDataPageIndex){
            mAdapter.notifyDataSetChanged();
            needUpdateDataPageIndex=-1;
        }

    }


log ： 0811 
添加多语言切换
文件夹 ： value-en


log: 0818
A26 输入法，开机后，隔一段时间，原本在桌面上显示后，就消失了
解决第二次进去launcher时，死掉的问题

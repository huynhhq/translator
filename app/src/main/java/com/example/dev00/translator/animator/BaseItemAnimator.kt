package com.example.dev00.translator.animator

import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.SimpleItemAnimator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.View
import com.example.dev00.translator.interfaces.AnimateViewHolder
import com.example.dev00.translator.helpers.ViewHelper


abstract class BaseItemAnimator : SimpleItemAnimator{

    private val DEBUG = false

    private var mPendingRemovals: ArrayList<ViewHolder> = ArrayList()
    private var mPendingAdditions: ArrayList<ViewHolder> = ArrayList()
    private var mPendingMoves: ArrayList<MoveInfo> = ArrayList<MoveInfo>()
    private var mPendingChanges: ArrayList<ChangeInfo> = ArrayList()

    private var mAdditionsList: ArrayList<ArrayList<ViewHolder>> = ArrayList()
    private var mMovesList: ArrayList<ArrayList<MoveInfo>> = ArrayList()
    private var mChangesList: ArrayList<ArrayList<ChangeInfo>> = ArrayList()

    protected var mAddAnimations: ArrayList<ViewHolder> = ArrayList()
    private var mMoveAnimations: ArrayList<ViewHolder> = ArrayList()
    protected var mRemoveAnimations: ArrayList<ViewHolder> = ArrayList()
    private var mChangeAnimations: ArrayList<ViewHolder> = ArrayList()

    protected var mInterpolator: Interpolator = DecelerateInterpolator()

    constructor(){
        supportsChangeAnimations = false
    }

    companion object{
        private class MoveInfo constructor(var holder: RecyclerView.ViewHolder
                                                   , var fromX: Int
                                                   , var fromY: Int
                                                   , var toX: Int
                                                   , var toY: Int)

        private class ChangeInfo constructor(var oldHolder: RecyclerView.ViewHolder?
                                                     , var newHolder: ViewHolder?
                                                     , var fromX: Int = 0
                                                     , var fromY: Int = 0
                                                     , var toX: Int = 0
                                                     , var toY: Int = 0)

        open class VpaListenerAdapter : ViewPropertyAnimatorListener {

            override fun onAnimationStart(view: View) {}

            override fun onAnimationEnd(view: View) {}

            override fun onAnimationCancel(view: View) {}
        }
    }

    override fun animateAdd(holder: ViewHolder): Boolean {
        endAnimation(holder);
        preAnimateAdd(holder);
        mPendingAdditions.add(holder);
        return true;
    }

    override fun runPendingAnimations() {
        val removalsPending = !mPendingRemovals.isEmpty()
        val movesPending = !mPendingMoves.isEmpty()
        val changesPending = !mPendingChanges.isEmpty()
        val additionsPending = !mPendingAdditions.isEmpty()
        if (!removalsPending && !movesPending && !additionsPending && !changesPending) {
            // nothing to animate
            return
        }
        // First, remove stuff
        for (holder in mPendingRemovals) {
            doAnimateRemove(holder)
        }
        mPendingRemovals.clear()
        // Next, move stuff
        if (movesPending) {
            val moves = java.util.ArrayList<MoveInfo>()
            moves.addAll(mPendingMoves)
            mMovesList.add(moves)
            mPendingMoves.clear()
            val mover = Runnable {
                val removed = mMovesList.remove(moves)
                if (!removed) {
                    // already canceled
                    return@Runnable
                }
                for (moveInfo in moves) {
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX,
                            moveInfo.toY)
                }
                moves.clear()
            }
            if (removalsPending) {
                val view = moves[0].holder.itemView
                ViewCompat.postOnAnimationDelayed(view, mover, removeDuration)
            } else {
                mover.run()
            }
        }
        // Next, change stuff, to run in parallel with move animations
        if (changesPending) {
            val changes = java.util.ArrayList<ChangeInfo>()
            changes.addAll(mPendingChanges)
            mChangesList.add(changes)
            mPendingChanges.clear()
            val changer = Runnable {
                val removed = mChangesList.remove(changes)
                if (!removed) {
                    // already canceled
                    return@Runnable
                }
                for (change in changes) {
                    animateChangeImpl(change)
                }
                changes.clear()
            }
            if (removalsPending) {
                val holder = changes[0].oldHolder
                ViewCompat.postOnAnimationDelayed(holder!!.itemView, changer, removeDuration)
            } else {
                changer.run()
            }
        }
        // Next, add stuff
        if (additionsPending) {
            val additions = ArrayList<ViewHolder>()
            additions.addAll(mPendingAdditions)
            mAdditionsList.add(additions)
            mPendingAdditions.clear()
            val adder = Runnable {
                val removed = mAdditionsList.remove(additions)
                if (!removed) {
                    // already canceled
                    return@Runnable
                }
                for (holder in additions) {
                    doAnimateAdd(holder)
                }
                additions.clear()
            }
            if (removalsPending || movesPending || changesPending) {
                val removeDuration = (if (removalsPending) removeDuration else 0).toLong()
                val moveDuration = (if (movesPending) moveDuration else 0).toLong()
                val changeDuration = (if (changesPending) changeDuration else 0).toLong()
                val totalDelay = removeDuration + Math.max(moveDuration, changeDuration)
                val view = additions[0].itemView
                ViewCompat.postOnAnimationDelayed(view, adder, totalDelay)
            } else {
                adder.run()
            }
        }
    }

    override fun animateMove(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        val view = holder.itemView
        var fromXTrans = fromX + ViewCompat.getTranslationX(holder.itemView).toInt()
        var fromYTrans = fromY + ViewCompat.getTranslationY(holder.itemView).toInt()
        endAnimation(holder)
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder)
            return false
        }
        if (deltaX != 0) {
            ViewCompat.setTranslationX(view, -deltaX.toFloat())
        }
        if (deltaY != 0) {
            ViewCompat.setTranslationY(view, -deltaY.toFloat())
        }
        mPendingMoves.add(MoveInfo(holder, fromXTrans, fromYTrans, toX, toY))
        return true
    }

    override fun animateChange(oldHolder: ViewHolder
                               , newHolder: ViewHolder
                               , fromX: Int
                               , fromY: Int
                               , toX: Int
                               , toY: Int): Boolean {
        if (oldHolder === newHolder) {
            // Don't know how to run change animations when the same view holder is re-used.
            // run a move animation to handle position changes.
            return animateMove(oldHolder, fromX, fromY, toX, toY)
        }
        val prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView)
        val prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView)
        val prevAlpha = ViewCompat.getAlpha(oldHolder.itemView)
        endAnimation(oldHolder)
        val deltaX = (toX.toFloat() - fromX.toFloat() - prevTranslationX).toInt()
        val deltaY = (toY.toFloat() - fromY.toFloat() - prevTranslationY).toInt()
        // recover prev translation state after ending animation
        ViewCompat.setTranslationX(oldHolder.itemView, prevTranslationX)
        ViewCompat.setTranslationY(oldHolder.itemView, prevTranslationY)
        ViewCompat.setAlpha(oldHolder.itemView, prevAlpha)
        if (newHolder != null && newHolder.itemView != null) {
            // carry over translation values
            endAnimation(newHolder)
            ViewCompat.setTranslationX(newHolder.itemView, -deltaX. toFloat())
            ViewCompat.setTranslationY(newHolder.itemView, -deltaY.toFloat())
            ViewCompat.setAlpha(newHolder.itemView, 0f)
        }
        mPendingChanges.add(ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY))
        return true
    }

    override fun isRunning(): Boolean {
        return !mPendingAdditions.isEmpty() ||
                !mPendingChanges.isEmpty() ||
                !mPendingMoves.isEmpty() ||
                !mPendingRemovals.isEmpty() ||
                !mMoveAnimations.isEmpty() ||
                !mRemoveAnimations.isEmpty() ||
                !mAddAnimations.isEmpty() ||
                !mChangeAnimations.isEmpty() ||
                !mMovesList.isEmpty() ||
                !mAdditionsList.isEmpty() ||
                !mChangesList.isEmpty()
    }

    override fun endAnimation(item: ViewHolder) {
        val view = item.itemView
        // this will trigger end callback which should set properties to their target values.
        ViewCompat.animate(view).cancel()
        // TODO if some other animations are chained to end, how do we cancel them as well?
        for (i in mPendingMoves.indices.reversed()) {
            val moveInfo = mPendingMoves[i]
            if (moveInfo.holder === item) {
                ViewCompat.setTranslationY(view, 0f)
                ViewCompat.setTranslationX(view, 0f)
                dispatchMoveFinished(item)
                mPendingMoves.removeAt(i)
            }
        }
        endChangeAnimation(mPendingChanges, item)
        if (mPendingRemovals.remove(item)) {
            ViewHelper.clear(item.itemView)
            dispatchRemoveFinished(item)
        }
        if (mPendingAdditions.remove(item)) {
            ViewHelper.clear(item.itemView)
            dispatchAddFinished(item)
        }

        for (i in mChangesList.indices.reversed()) {
            val changes = mChangesList[i]
            endChangeAnimation(changes, item)
            if (changes.isEmpty()) {
                mChangesList.removeAt(i)
            }
        }
        for (i in mMovesList.indices.reversed()) {
            val moves = mMovesList[i]
            for (j in moves.indices.reversed()) {
                val moveInfo = moves[j]
                if (moveInfo.holder === item) {
                    ViewCompat.setTranslationY(view, 0f)
                    ViewCompat.setTranslationX(view, 0f)
                    dispatchMoveFinished(item)
                    moves.removeAt(j)
                    if (moves.isEmpty()) {
                        mMovesList.removeAt(i)
                    }
                    break
                }
            }
        }
        for (i in mAdditionsList.indices.reversed()) {
            val additions = mAdditionsList[i]
            if (additions.remove(item)) {
                ViewHelper.clear(item.itemView)
                dispatchAddFinished(item)
                if (additions.isEmpty()) {
                    mAdditionsList.removeAt(i)
                }
            }
        }

        // animations should be ended by the cancel above.
        if (mRemoveAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                    "after animation is cancelled, item should not be in " + "mRemoveAnimations list")
        }

        if (mAddAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                    "after animation is cancelled, item should not be in " + "mAddAnimations list")
        }

        if (mChangeAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                    "after animation is cancelled, item should not be in " + "mChangeAnimations list")
        }

        if (mMoveAnimations.remove(item) && DEBUG) {
            throw IllegalStateException(
                    "after animation is cancelled, item should not be in " + "mMoveAnimations list")
        }
        dispatchFinishedWhenDone()
    }

    override fun animateRemove(holder: ViewHolder): Boolean {
        endAnimation(holder)
        preAnimateRemove(holder)
        mPendingRemovals.add(holder)
        return true
    }

    override fun endAnimations() {
        var count = mPendingMoves.size
        for (i in count - 1 downTo 0) {
            val item = mPendingMoves[i]
            val view = item.holder.itemView
            ViewCompat.setTranslationY(view, 0f)
            ViewCompat.setTranslationX(view, 0f)
            dispatchMoveFinished(item.holder)
            mPendingMoves.removeAt(i)
        }
        count = mPendingRemovals.size
        for (i in count - 1 downTo 0) {
            val item = mPendingRemovals[i]
            dispatchRemoveFinished(item)
            mPendingRemovals.removeAt(i)
        }
        count = mPendingAdditions.size
        for (i in count - 1 downTo 0) {
            val item = mPendingAdditions[i]
            ViewHelper.clear(item.itemView)
            dispatchAddFinished(item)
            mPendingAdditions.removeAt(i)
        }
        count = mPendingChanges.size
        for (i in count - 1 downTo 0) {
            endChangeAnimationIfNecessary(mPendingChanges[i])
        }
        mPendingChanges.clear()
        if (!isRunning) {
            return
        }

        var listCount = mMovesList.size
        for (i in listCount - 1 downTo 0) {
            val moves = mMovesList[i]
            count = moves.size
            for (j in count - 1 downTo 0) {
                val moveInfo = moves[j]
                val item = moveInfo.holder
                val view = item.itemView
                ViewCompat.setTranslationY(view, 0f)
                ViewCompat.setTranslationX(view, 0f)
                dispatchMoveFinished(moveInfo.holder)
                moves.removeAt(j)
                if (moves.isEmpty()) {
                    mMovesList.remove(moves)
                }
            }
        }
        listCount = mAdditionsList.size
        for (i in listCount - 1 downTo 0) {
            val additions = mAdditionsList[i]
            count = additions.size
            for (j in count - 1 downTo 0) {
                val item = additions[j]
                val view = item.itemView
                ViewCompat.setAlpha(view, 1f)
                dispatchAddFinished(item)
                //this check prevent exception when removal already happened during finishing animation
                if (j < additions.size) {
                    additions.removeAt(j)
                }
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions)
                }
            }
        }
        listCount = mChangesList.size
        for (i in listCount - 1 downTo 0) {
            val changes = mChangesList[i]
            count = changes.size
            for (j in count - 1 downTo 0) {
                endChangeAnimationIfNecessary(changes[j])
                if (changes.isEmpty()) {
                    mChangesList.remove(changes)
                }
            }
        }

        cancelAll(mRemoveAnimations)
        cancelAll(mMoveAnimations)
        cancelAll(mAddAnimations)
        cancelAll(mChangeAnimations)

        dispatchAnimationsFinished()
    }



    protected fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder) {}

    protected abstract fun preAnimateAddImpl(holder: RecyclerView.ViewHolder)

    protected abstract fun animateRemoveImpl(holder: RecyclerView.ViewHolder)

    protected abstract fun animateAddImpl(holder: RecyclerView.ViewHolder)

    private fun preAnimateRemove(holder: RecyclerView.ViewHolder) {
        ViewHelper.clear(holder.itemView)

        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).preAnimateRemoveImpl(holder)
        } else {
            preAnimateRemoveImpl(holder)
        }
    }

    private fun preAnimateAdd(holder: RecyclerView.ViewHolder) {
        ViewHelper.clear(holder.itemView)

        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).preAnimateAddImpl(holder)
        } else {
            preAnimateAddImpl(holder)
        }
    }

    private fun doAnimateRemove(holder: RecyclerView.ViewHolder) {
        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).animateRemoveImpl(holder, DefaultRemoveVpaListener(holder))
        } else {
            animateRemoveImpl(holder)
        }

        mRemoveAnimations.add(holder)
    }

    private fun doAnimateAdd(holder: RecyclerView.ViewHolder) {
        if (holder is AnimateViewHolder) {
            (holder as AnimateViewHolder).animateAddImpl(holder, DefaultAddVpaListener(holder))
        } else {
            animateAddImpl(holder)
        }

        mAddAnimations.add(holder)
    }

    protected inner class DefaultAddVpaListener(
            var mViewHolder: RecyclerView.ViewHolder) : VpaListenerAdapter() {

        override fun onAnimationStart(view: View) {
            dispatchAddStarting(mViewHolder)
        }

        override fun onAnimationCancel(view: View) {
            ViewHelper.clear(view)
        }

        override fun onAnimationEnd(view: View) {
            ViewHelper.clear(view)
            dispatchAddFinished(mViewHolder)
            mAddAnimations.remove(mViewHolder)
            dispatchFinishedWhenDone()
        }
    }

    protected inner class DefaultRemoveVpaListener(
            var mViewHolder: RecyclerView.ViewHolder) : VpaListenerAdapter() {

        override fun onAnimationStart(view: View) {
            dispatchRemoveStarting(mViewHolder)
        }

        override fun onAnimationCancel(view: View) {
            ViewHelper.clear(view)
        }

        override fun onAnimationEnd(view: View) {
            ViewHelper.clear(view)
            dispatchRemoveFinished(mViewHolder)
            mRemoveAnimations.remove(mViewHolder)
            dispatchFinishedWhenDone()
        }
    }

    /**
     * Check the state of currently pending and running animations. If there are none
     * pending/running, call #dispatchAnimationsFinished() to notify any
     * listeners.
     */
    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    private fun animateMoveImpl(holder: ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val view = holder.itemView
        val deltaX = toX - fromX
        val deltaY = toY - fromY
        if (deltaX != 0) {
            ViewCompat.animate(view).translationX(0f)
        }
        if (deltaY != 0) {
            ViewCompat.animate(view).translationY(0f)
        }
        // TODO: make EndActions end listeners instead, since end actions aren't called when
        // vpas are canceled (and can't end them. why?)
        // need listener functionality in VPACompat for this. Ick.
        mMoveAnimations.add(holder)
        val animation = ViewCompat.animate(view)
        animation.setDuration(moveDuration).setListener(object : VpaListenerAdapter() {
            override fun onAnimationStart(view: View) {
                dispatchMoveStarting(holder)
            }

            override fun onAnimationCancel(view: View) {
                if (deltaX != 0) {
                    ViewCompat.setTranslationX(view, 0f)
                }
                if (deltaY != 0) {
                    ViewCompat.setTranslationY(view, 0f)
                }
            }

            override fun onAnimationEnd(view: View) {
                animation.setListener(null)
                dispatchMoveFinished(holder)
                mMoveAnimations.remove(holder)
                dispatchFinishedWhenDone()
            }
        }).start()
    }

    private fun animateChangeImpl(changeInfo: ChangeInfo) {
        val holder = changeInfo.oldHolder
        val view = holder?.itemView
        val newHolder = changeInfo.newHolder
        val newView = newHolder?.itemView
        if (view != null) {
            mChangeAnimations.add(changeInfo.oldHolder!!)
            val oldViewAnim = ViewCompat.animate(view).setDuration(changeDuration)
            oldViewAnim.translationX((changeInfo.toX - changeInfo.fromX).toFloat())
            oldViewAnim.translationY((changeInfo.toY - changeInfo.fromY).toFloat())
            oldViewAnim.alpha(0f).setListener(object : VpaListenerAdapter() {
                override fun onAnimationStart(view: View) {
                    dispatchChangeStarting(changeInfo.oldHolder, true)
                }

                override fun onAnimationEnd(view: View) {
                    oldViewAnim.setListener(null)
                    ViewCompat.setAlpha(view, 1f)
                    ViewCompat.setTranslationX(view, 0f)
                    ViewCompat.setTranslationY(view, 0f)
                    dispatchChangeFinished(changeInfo.oldHolder, true)
                    mChangeAnimations.remove(changeInfo.oldHolder!!)
                    dispatchFinishedWhenDone()
                }
            }).start()
        }
        if (newView != null) {
            mChangeAnimations.add(changeInfo.newHolder!!)
            val newViewAnimation = ViewCompat.animate(newView)
            newViewAnimation.translationX(0f).translationY(0f).setDuration(changeDuration).alpha(1f).setListener(object : VpaListenerAdapter() {
                override fun onAnimationStart(view: View) {
                    dispatchChangeStarting(changeInfo.newHolder, false)
                }

                override fun onAnimationEnd(view: View) {
                    newViewAnimation.setListener(null)
                    ViewCompat.setAlpha(newView, 1f)
                    ViewCompat.setTranslationX(newView, 0f)
                    ViewCompat.setTranslationY(newView, 0f)
                    dispatchChangeFinished(changeInfo.newHolder, false)
                    mChangeAnimations.remove(changeInfo.newHolder!!)
                    dispatchFinishedWhenDone()
                }
            }).start()
        }
    }

    private fun endChangeAnimation(infoList: MutableList<ChangeInfo>, item: ViewHolder) {
        for (i in infoList.indices.reversed()) {
            val changeInfo = infoList[i]
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo)
                }
            }
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo, item: ViewHolder): Boolean {
        var oldItem = false
        if (changeInfo.newHolder === item) {
            changeInfo.newHolder = null
        } else if (changeInfo.oldHolder === item) {
            changeInfo.oldHolder = null
            oldItem = true
        } else {
            return false
        }
        ViewCompat.setAlpha(item.itemView, 1f)
        ViewCompat.setTranslationX(item.itemView, 0f)
        ViewCompat.setTranslationY(item.itemView, 0f)
        dispatchChangeFinished(item, oldItem)
        return true
    }

    internal fun cancelAll(viewHolders: List<ViewHolder>) {
        for (i in viewHolders.indices.reversed()) {
            ViewCompat.animate(viewHolders[i].itemView).cancel()
        }
    }

    private fun endChangeAnimationIfNecessary(changeInfo: ChangeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder!!)
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder!!)
        }
    }

    protected fun getAddDelay(holder: RecyclerView.ViewHolder): Long {
        return Math.abs(holder.adapterPosition * addDuration / 4)
    }

    protected fun getRemoveDelay(holder: RecyclerView.ViewHolder): Long {
        return Math.abs(holder.oldPosition * removeDuration / 4)
    }
}
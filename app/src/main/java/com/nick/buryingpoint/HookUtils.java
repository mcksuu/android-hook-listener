package com.nick.buryingpoint;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Created by nick on 2017/3/17.
 */

public class HookUtils {
    private static final String VIEW_CLASS = "android.view.View";

    /**
     * @param mActivity
     * @param onClickListener
     */
    public static void hookListener(Activity mActivity, OnClickListener onClickListener) {
        if (mActivity != null) {
            View decorView = mActivity.getWindow().getDecorView();
            getView(decorView, onClickListener);
        }
    }

    /**
     * 递归进行viewHook
     * @param view
     * @param onClickListener
     */
    private static void getView(View view, OnClickListener onClickListener) {
        //递归遍历，判断当前view是不是ViewGroup，如果是继续遍历，知道不是为止
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                getView(((ViewGroup) view).getChildAt(i), onClickListener);
            }
        }
        viewHook(view, onClickListener);
    }

    /**
     * 通过反射将我们的代理类替换原来的onClickListener
     *
     * @param view
     * @param onClickListener
     */
    private static void viewHook(View view, OnClickListener onClickListener) {
        try {
            Class viewClass = Class.forName(VIEW_CLASS);//反射创建View
            Field listenerInfoField = viewClass.getDeclaredField("mListenerInfo");//获得View属性mListenerInfo
            listenerInfoField.setAccessible(true);
            Object mListenerInfo = listenerInfoField.get(view);//ListenerInfo==>>View对象中的mListenerInfo的实例

            if (mListenerInfo != null) {
                Class listenerInfo2 = Class.forName("android.view.View$ListenerInfo");//反射创建ListenerInfo
                Field onClickListenerFiled = listenerInfo2.getDeclaredField("mOnClickListener");//获得ListenerInfo属性mOnClickListener
                onClickListenerFiled.setAccessible(true);
                View.OnClickListener o1 = (View.OnClickListener) onClickListenerFiled.get(mListenerInfo);//获得mListenerInfo的实例中的mOnClickListener实例
                if (o1 != null) {
                    View.OnClickListener onClickListenerProxy = new OnClickListenerProxy(o1, onClickListener);
                    onClickListenerFiled.set(mListenerInfo, onClickListenerProxy);//设置ListenerInfo属性mOnClickListener为我们的代理listener
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public interface OnClickListener {
        void beforeInListener(View v);
        void afterInListener(View v);
    }

    private static class OnClickListenerProxy implements View.OnClickListener {
        private View.OnClickListener object;
        private HookUtils.OnClickListener mListener;

        public OnClickListenerProxy(View.OnClickListener object, HookUtils.OnClickListener listener) {
            this.object = object;
            this.mListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.beforeInListener(v);
            }
            if (object != null) {
                object.onClick(v);
            }
            if (mListener != null) {
                mListener.afterInListener(v);
            }
        }
    }
}

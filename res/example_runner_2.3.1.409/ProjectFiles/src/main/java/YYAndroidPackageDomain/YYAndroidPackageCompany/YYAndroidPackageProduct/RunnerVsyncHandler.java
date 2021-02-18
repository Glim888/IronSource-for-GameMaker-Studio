package ${YYAndroidPackageName};


import android.annotation.TargetApi;


import android.view.Choreographer;




@TargetApi(16)
public class RunnerVsyncHandler implements Choreographer.FrameCallback
{
	public static final class Accessor { private Accessor() {} }
	private static final Accessor accessor = new Accessor();

	@Override
    public void doFrame(long frameTimeNanos) {
        // The events should not start until the RenderThread is created, and should stop
        // before it's nulled out.

		DemoGLSurfaceView mGLView = RunnerActivity.CurrentActivity.GetGLView(accessor);

		if ((mGLView != null) && (mGLView.mRenderer != null))
		{									
			DemoRenderer.elapsedVsyncs++;

			/*if (DemoRenderer.waiterObject != null)
			{
				synchronized(DemoRenderer.waiterObject)
				{
					DemoRenderer.waiterObject.notifyAll();					
				}
			}*/
		}

		//Log.i("yoyo", "doFrame: " + frameTimeNanos);
		Choreographer.getInstance().postFrameCallback(this);
	}

	public void PostFrameCallback()
	{
		Choreographer.getInstance().postFrameCallback(this);
	}

	public void RemoveFrameCallback()
	{
		Choreographer.getInstance().removeFrameCallback(this);
	}
}
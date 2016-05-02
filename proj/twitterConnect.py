import tweepy
from tweepy import OAuthHandler
 
consumer_key = 'awDiWluClxz4ehXBpHrojhXne'
consumer_secret = '4srjdSnLzXkG9oiUDoXk8k1VKIUEuW6a9DtEVxmQbrtRb967XL'
access_token = '2152052418-wBo4B2nXE25zTiFNtxtPpXzPtYCYRaBQOhMy7Fr'
access_secret = '4YyyhdXqkqArlJAv2Oy41UpTjPoYkKh5b6n6kLwTTveRX'
 
auth = OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_secret)


api = tweepy.API(auth)


#user = api.get_user('@investorslive')
print api.me()

